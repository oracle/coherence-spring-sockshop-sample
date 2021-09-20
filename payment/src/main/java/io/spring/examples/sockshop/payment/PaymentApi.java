/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

public interface PaymentApi {
	@GetMapping(value = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return the payment authorization for the specified order")
	ResponseEntity<Collection<? extends Authorization>> getOrderAuthorizations(@Parameter(description = "Order identifier")
																			   @PathVariable("orderId") String orderId);

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Authorize a payment request")
	Authorization authorize(@Parameter(description = "Payment request")
							@RequestBody PaymentRequest paymentRequest);
}
