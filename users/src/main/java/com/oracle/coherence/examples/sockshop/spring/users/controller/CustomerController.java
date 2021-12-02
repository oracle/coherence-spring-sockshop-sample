/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.Card;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(
		path = "/customers",
		consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE},
		produces = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE})
public class CustomerController {

	@Autowired
	private UserService userService;


	@GetMapping
	@Operation(summary = "Return all customers; or empty collection if no customer found")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public CollectionModel<User> getAllCustomers() {
		final Collection<User> users = this.userService.getAllUsers();
		for (final User user : users) {
			final Link selfLink = linkTo(methodOn(CustomerController.class)
					.getCustomer(user.getUsername())).withSelfRel();
			user.add(selfLink);
		}
		final Link link = linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel();
		final CollectionModel<User> result = CollectionModel.of(users, link);
		return result;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Return customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public User getCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		return userService.getOrCreate(id);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the delete is successful")
	})
	//@NewSpan
	public BooleanStatusResponse deleteCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		User prev = this.userService.removeUser(id);
		return new BooleanStatusResponse(prev != null);
	}

	//
	@GetMapping("/{id}/cards")
	@Operation(summary = "Return all cards for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public CollectionModel<Object> getCustomerCards(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);
		final Link link = linkTo(methodOn(CustomerController.class)
				.getCustomerCards(user.getUsername())).withSelfRel();

		List<Card> cards = user.getCards();
		Iterable<Object> content = (cards != null && !cards.isEmpty())
				? new ArrayList<>(cards)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Card.class));
		CollectionModel<Object> result = CollectionModel.of(content, link);
		return result;
	}

	@GetMapping("/{id}/addresses")
	@Operation(summary = "Return all addresses for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	//@NewSpan
	public Object getCustomerAddresses(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);
		final Link link = linkTo(methodOn(CustomerController.class)
				.getCustomerAddresses(user.getUsername())).withSelfRel();
		List<Address> addresses = user.getAddresses();
		Iterable<Object> content = (addresses != null && !addresses.isEmpty())
				? new ArrayList<>(addresses)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Address.class));
		CollectionModel<Object> result = CollectionModel.of(content, link);
		return result;
	}
}
