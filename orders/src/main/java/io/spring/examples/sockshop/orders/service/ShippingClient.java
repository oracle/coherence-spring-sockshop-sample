/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders.service;

import io.spring.examples.sockshop.orders.model.ShippingRequest;
import io.spring.examples.sockshop.orders.model.Shipment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "shipping", url = "http://shipping/shipping", primary = false)
public interface ShippingClient {
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Shipment ship(@RequestBody ShippingRequest request);
}
