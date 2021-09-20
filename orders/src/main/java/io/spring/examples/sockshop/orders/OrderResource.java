/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import io.micrometer.core.annotation.Timed;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Orders Service REST API.
 */
@Log
@Scope(BeanDefinition.SCOPE_SINGLETON)
@RequestMapping("/orders")
@RestController
public class OrderResource implements OrderApi {
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

    @Override
    public ResponseEntity<Map<String, Map<String, Object>>> getOrdersForCustomer(String customerId) {
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

    @Override
    public ResponseEntity<Order> getOrder(String orderId) {
        Order order = orders.get(orderId);
        return order == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(order);
    }

    @Override
    @Timed("order.new")
    public ResponseEntity<Order> newOrder(NewOrderRequest request) {
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
        Address    address  = usersService.address(addressPath.substring(11));
        Card       card     = usersService.card(cardPath.substring(7));
        Customer   customer = usersService.customer(customerPath.substring(11));

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
