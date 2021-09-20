/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface OrderApi {
	@GetMapping(value = "search/customerId", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return the orders for the specified customer")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if orders exist"),
			@ApiResponse(responseCode = "404", description = "if orders do not exist")
	})
	ResponseEntity<Map<String, Map<String, Object>>> getOrdersForCustomer(@Parameter(description = "Customer identifier")
																		  @RequestParam("custId") String customerId);

	@GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return the order for the specified order")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the order exist"),
			@ApiResponse(responseCode = "404", description = "if the order doesn't exist")
	})
	ResponseEntity<Order> getOrder(@Parameter(description = "Order identifier")
								   @PathVariable("id") String orderId);

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Place a new order for the specified order request")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "if the request is successfully processed"),
			@ApiResponse(responseCode = "406", description = "if the payment is not authorized")
	})
	ResponseEntity<Order> newOrder(@Parameter(description = "Order request") @RequestBody NewOrderRequest request);
}
