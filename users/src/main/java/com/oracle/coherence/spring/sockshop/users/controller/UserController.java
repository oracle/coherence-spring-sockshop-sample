/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.controller.support.IdStatusResponse;
import com.oracle.coherence.spring.sockshop.users.controller.support.exceptions.UserAlreadyExistsException;
import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.model.WrappedUser;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
		consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE},
		produces = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE}
)
//@ExposesResourceFor(User.class)
public class UserController {

	@Autowired
	private UserService users;

	//@NewSpan
	@Operation(summary = "Basic user authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully authenticated"),
			@ApiResponse(responseCode = "401", description = "if authentication fails")
	})
	@GetMapping("/login")
	public WrappedUser login(@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
		User userFromCoherence = this.users.getUser(securityUser.getUsername());
		final WrappedUser user = new WrappedUser();
		user.setUsername(userFromCoherence.getUsername());
		user.setFirstName(userFromCoherence.getFirstName());
		user.setLastName(userFromCoherence.getLastName());
		return user;
	}

	//@NewSpan
	@PostMapping("/register")
	@Operation(summary = "Register a user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if user is successfully registered"),
			@ApiResponse(responseCode = "409", description = "if the user is already registered")
	})
	public IdStatusResponse register(
			@RequestBody
			@Parameter(description = "The user to be registered")
			com.oracle.coherence.spring.sockshop.users.model.User user) {
		com.oracle.coherence.spring.sockshop.users.model.User prev = users.register(user);
		if (prev != null) {
			throw new UserAlreadyExistsException();
		}
		return new IdStatusResponse(user.getUsername());
	}
}
