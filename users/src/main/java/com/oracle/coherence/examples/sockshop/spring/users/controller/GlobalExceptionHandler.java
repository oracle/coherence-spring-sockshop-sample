/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.AddressNotFoundException;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.CardNotFoundException;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("User with that ID already exists");
	}

	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<String> handleAddressNotFoundException(AddressNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
	}

	@ExceptionHandler(CardNotFoundException.class)
	public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found.");
	}
}
