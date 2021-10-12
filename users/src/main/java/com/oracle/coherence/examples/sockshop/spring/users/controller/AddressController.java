/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.support.IdStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.AddressId;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.AddressNotFoundException;

import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
		path = "/addresses",
		consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE},
		produces = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE})
public class AddressController {

	@Autowired
	private UserService userService;

	@GetMapping
	@Operation(summary = "Return all addresses associated with a user; or an empty list if no address found")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	public CollectionModel<Address> getAllAddresses(
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User securityUser) {
		final User user = this.userService.getUser(securityUser.getUsername());
		return CollectionModel.of(user.getAddresses());
	}

	@PostMapping
	@Operation(summary = "Register address for a user; no-op if the address exist")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if address is successfully registered")
	})
	//@NewSpan
	public IdStatusResponse registerAddress(
			@Parameter(description = "Add Address request") @RequestBody AddAddressRequest req) {
		Address address = new Address(req.number, req.street, req.city, req.postcode, req.country);
		AddressId id = this.userService.addAddress(req.userID, address);
		return new IdStatusResponse(id.toString());
	}


	@GetMapping("/{id}")
	@Operation(summary = "Return addresses for the specified identifier")
//	@NewSpan
	public Address getAddress(@Parameter(description = "Address identifier")
					   @PathVariable("id") AddressId id) {
		final Address address = this.userService.getAddress(id);
		if (address != null) {
			return address;
		}
		else {
			throw new AddressNotFoundException();
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete address for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if address is successfully deleted")
	})
//	@NewSpan
	public BooleanStatusResponse deleteAddress(@Parameter(description = "Address identifier") @PathVariable("id") AddressId id) {
		BooleanStatusResponse booleanStatusResponse;
		try {
			this.userService.removeAddress(id);
			booleanStatusResponse = new BooleanStatusResponse(true);
		}
		catch (RuntimeException e) {
			booleanStatusResponse = new BooleanStatusResponse(false);
		}
		return booleanStatusResponse;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddAddressRequest {
		public String number;
		public String street;
		public String city;
		public String postcode;
		public String country;
		public String userID;
	}
}
