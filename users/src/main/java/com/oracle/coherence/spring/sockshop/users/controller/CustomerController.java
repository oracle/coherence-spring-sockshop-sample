/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.spring.sockshop.users.model.Address;
import com.oracle.coherence.spring.sockshop.users.model.Card;
import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private UserService userService;


	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return all customers; or empty collection if no customer found")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public ResponseEntity<CollectionModel<User>> getAllCustomers() {
		final Collection<User> users = this.userService.getAllUsers();
		for (final User user : users) {
			final Link selfLink = linkTo(methodOn(CustomerController.class)
					.getCustomer(user.getUsername())).withSelfRel();
			user.add(selfLink);
		}
		final Link link = linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel();
		final CollectionModel<User> result = CollectionModel.of(users, link);
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public ResponseEntity<User> getCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		return ResponseEntity.ok(userService.getOrCreate(id));
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the delete is successful")
	})
	//@NewSpan
	public ResponseEntity<BooleanStatusResponse> deleteCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		User prev = this.userService.removeUser(id);
		return ResponseEntity.ok(new BooleanStatusResponse(prev != null));
	}

	//
	@GetMapping(value = "/{id}/cards", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return all cards for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public ResponseEntity<CollectionModel<Object>> getCustomerCards(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);
		final Link link = linkTo(methodOn(CustomerController.class)
				.getCustomerCards(user.getUsername())).withSelfRel();

		List<Card> cards = user.getCards();
		Iterable<Object> content = (cards != null && !cards.isEmpty())
				? new ArrayList<>(cards)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Card.class));
		CollectionModel<Object> result = CollectionModel.of(content, link);
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/{id}/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return all addresses for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public ResponseEntity<Object> getCustomerAddresses(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);
		final Link link = linkTo(methodOn(CustomerController.class)
				.getCustomerAddresses(user.getUsername())).withSelfRel();
		List<Address> addresses = user.getAddresses();
		Iterable<Object> content = (addresses != null && !addresses.isEmpty())
				? new ArrayList<>(addresses)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Address.class));
		CollectionModel<Object> result = CollectionModel.of(content, link);
		return ResponseEntity.ok(result);
	}
}
