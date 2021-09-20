/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping
public class UserController {

	@Autowired
	private UserService users;

	//@NewSpan
	@GetMapping("login")
	@Operation(summary = "Basic user authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully authenticated"),
			@ApiResponse(responseCode = "401", description = "if authentication fail")
	})
	public ResponseEntity<?> login(@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
		return ResponseEntity.ok(Map.of("user", new User(securityUser.getUsername())));
	}

	class User {
		String id;

		public User(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	//@NewSpan
	@PostMapping("/register")
	@Operation(summary = "Register a user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully registered"),
			@ApiResponse(responseCode = "409", description = "if the user is already registered")
	})
	public ResponseEntity<?> register(@RequestBody @Parameter(description = "The user to be registered") com.oracle.coherence.spring.sockshop.users.model.User user) {
		com.oracle.coherence.spring.sockshop.users.model.User prev = users.register(user);
		if (prev != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with that ID already exists");
		}

		return ResponseEntity.ok(new User(user.getUsername()));
	}
}
