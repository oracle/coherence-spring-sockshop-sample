/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.controller;

import com.oracle.coherence.examples.sockshop.spring.orders.service.support.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class OrderExceptionHandler {

	@ExceptionHandler(value = {OrderException.class})
	@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
	protected ResponseEntity handleOrderException(OrderException ex) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("message", ex.getMessage()));
	}
}
