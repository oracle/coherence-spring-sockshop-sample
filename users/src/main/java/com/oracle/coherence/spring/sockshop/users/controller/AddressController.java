/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.spring.sockshop.users.controller.support.IdStatusResponse;
import com.oracle.coherence.spring.sockshop.users.model.Address;
import com.oracle.coherence.spring.sockshop.users.model.AddressId;
import com.oracle.coherence.spring.sockshop.users.repository.CoherenceUserRepository;

import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController implements AddressApi {

	@Autowired
	private UserService userService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return all addresses associated with a user; or an empty list if no address found")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	public ResponseEntity<List<Address>> getAllAddresses() {
		return new ResponseEntity<>(Collections.emptyList(), //TODO
				HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Register address for a user; no-op if the address exist")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if address is successfully registered")
	})
	//@NewSpan
	public ResponseEntity<IdStatusResponse> registerAddress(
			@Parameter(description = "Add Address request") @RequestBody AddAddressRequest req) {
		Address address = new Address(req.number, req.street, req.city, req.postcode, req.country);
		AddressId id = this.userService.addAddress(req.userID, address);
		return ResponseEntity.ok(new IdStatusResponse(id.toString()));
	}


	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return addresses for the specified identifier")
//	@NewSpan
	public Address getAddress(@Parameter(description = "Address identifier")
					   @PathVariable("id") AddressId id) {
		return this.userService.getAddress(id);
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete address for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if address is successfully deleted")
	})
//	@NewSpan
	public ResponseEntity deleteAddress(@Parameter(description = "Address identifier") @PathVariable("id") AddressId id) {
		try {
			this.userService.removeAddress(id);
			return ResponseEntity.ok(new BooleanStatusResponse(true));
		}
		catch (RuntimeException e) {
			return ResponseEntity.ok(new BooleanStatusResponse(false));
		}
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
