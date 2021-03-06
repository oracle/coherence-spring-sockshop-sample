/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.service;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Shipment;
import com.oracle.coherence.examples.sockshop.spring.orders.model.ShippingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "shipping", url = "http://shipping/shipping", primary = false)
public interface ShippingClient {
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	Shipment ship(@RequestBody ShippingRequest request);
}
