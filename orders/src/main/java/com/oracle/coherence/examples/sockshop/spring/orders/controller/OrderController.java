/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.controller;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Address;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Card;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Customer;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Item;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Order;
import io.micrometer.core.annotation.Timed;
import com.oracle.coherence.examples.sockshop.spring.orders.controller.support.NewOrderRequest;
import com.oracle.coherence.examples.sockshop.spring.orders.repository.OrderRepository;
import com.oracle.coherence.examples.sockshop.spring.orders.service.CartsClient;
import com.oracle.coherence.examples.sockshop.spring.orders.service.OrderProcessor;
import com.oracle.coherence.examples.sockshop.spring.orders.service.UsersClient;
import com.oracle.coherence.examples.sockshop.spring.orders.service.support.OrderException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Orders Service REST API.
 */
@Slf4j
@RequestMapping("/orders")
@RestController
public class OrderController {
    /**
     * Order repository to use.
     */
    @Autowired
    private OrderRepository orders;

    /**
     * Order processor to use.
     */
    @Autowired
    private OrderProcessor processor;

    @Autowired
    protected CartsClient cartsService;

    @Autowired
    protected UsersClient usersService;

    @GetMapping(value = "/search/customerId", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return the orders for the specified customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "if orders exist"),
            @ApiResponse(responseCode = "404", description = "if orders do not exist")
    })
    public ResponseEntity<Map<String, Map<String, Object>>> getOrdersForCustomer(
            @Parameter(description = "Customer identifier")
            @RequestParam("custId") String customerId) {
        Collection<? extends Order> customerOrders = orders.findOrdersByCustomer(customerId);
        if (customerOrders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return wrap(customerOrders);
    }

    private ResponseEntity<Map<String, Map<String, Object>>> wrap(Object value) {
        Map<String, Map<String, Object>> map = Collections.singletonMap("_embedded", Collections.singletonMap("customerOrders", value));
        return ResponseEntity.ok(map);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return the order for the specified order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "if the order exist"),
            @ApiResponse(responseCode = "404", description = "if the order doesn't exist")
    })
    public ResponseEntity<Order> getOrder(
            @Parameter(description = "Order identifier")
            @PathVariable("id") String orderId) {
        Order order = orders.get(orderId);
        return order == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(order);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Place a new order for the specified order request")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "if the request is successfully processed"),
            @ApiResponse(responseCode = "406", description = "if the payment is not authorized")
    })
    @Timed("order.new")
    public ResponseEntity<Order> newOrder(@Parameter(description = "Order request") @RequestBody NewOrderRequest request) {
        log.info("Processing new order: " + request);

        if (request.address == null || request.customer == null || request.card == null || request.items == null) {
            throw new InvalidOrderException("Invalid order request. Order requires customer, address, card and items.");
        }

        String itemsPath = request.items.getPath();
        String addressPath = request.address.getPath();
        String cardPath = request.card.getPath();
        String customerPath = request.customer.getPath();
        if (!itemsPath.startsWith("/carts/") || !itemsPath.endsWith("/items") ||
            !addressPath.startsWith("/addresses/") ||
            !cardPath.startsWith("/cards/") ||
            !customerPath.startsWith("/customers/")) {
            throw new InvalidOrderException("Invalid order request. Order requires the URIs to have path /customers/xxx, /addresses/xxx, /cards/xxx and /carts/xxx/items.");
        }

        List<Item> items    = cartsService.cart(itemsPath.substring(7, itemsPath.length() - 6));
        Address address  = usersService.address(addressPath.substring(11));
        Card card     = usersService.card(cardPath.substring(7));
        Customer customer = usersService.customer(customerPath.substring(11));

        Order order = Order.builder()
                .customer(customer)
                .address(address)
                .card(card)
                .items(items)
                .build();

        processor.processOrder(order);

        log.info("Created Order: " + order.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // ---- inner class: InvalidOrderException ------------------------------

    /**
     * An exception that is thrown if the arguments in the {@code NewOrderRequest}
     * are invalid.
     */
    public static class InvalidOrderException extends OrderException {
        public InvalidOrderException(String s) {
            super(s);
        }
    }
}
