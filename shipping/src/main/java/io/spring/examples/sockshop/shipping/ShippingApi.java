/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.shipping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ShippingApi {

	@GetMapping(value = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return the Shipment for the specified order")
	Shipment getShipmentByOrderId(@Parameter(description = "Order identifier")
								  @PathVariable("orderId") String orderId);

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Ship the specified shipping request")
	Shipment ship(@Parameter(description = "Shipping request")
				  @RequestBody ShippingRequest req);
}
