/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates.
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
import org.springframework.hateoas.EntityModel;
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
import java.util.stream.Collectors;

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
	public CollectionModel<EntityModel<User>> getAllCustomers() {
		final CustomerController controller = methodOn(CustomerController.class);

		final Collection<EntityModel<User>> users = this.userService.getAllUsers()
				.stream().map(user -> {
					final Link selfLink = linkTo(controller.getCustomer(user.getId())).withSelfRel();
					return EntityModel.of(user).add(selfLink);
				}).collect(Collectors.toList());

		final Link link = linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel();
		return CollectionModel.of(users, link);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Return customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	public EntityModel<User> getCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final CustomerController controller = methodOn(CustomerController.class);

		final Link selfLink = linkTo(controller.getCustomer(id)).withSelfRel();
		final Link customerLink = linkTo(controller.getCustomer(id)).withRel("customer");
		final Link addressesLink = linkTo(controller.getCustomerAddresses(id)).withRel("addresses");
		final Link cardsLink = linkTo(controller.getCustomerCards(id)).withRel("cards");

		return EntityModel.of(userService.getOrCreate(id))
				.add(selfLink, customerLink, addressesLink, cardsLink);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete customer for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the delete is successful")
	})
	public BooleanStatusResponse deleteCustomer(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User prev = this.userService.removeUser(id);
		return new BooleanStatusResponse(prev != null);
	}

	@GetMapping("/{id}/cards")
	@Operation(summary = "Return all cards for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	public CollectionModel<Object> getCustomerCards(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);

		final CustomerController controller = methodOn(CustomerController.class);
		final Link link = linkTo(controller
				.getCustomerCards(user.getUsername())).withSelfRel();

		final CardController cardController = methodOn(CardController.class);

		final List<EntityModel<Card>> cards = user.getCards()
				.stream().map(card -> EntityModel.of(card).add(linkTo(cardController.getCard(card.getCardId())).withSelfRel()))
				.collect(Collectors.toList());
		final Iterable<Object> content = (cards != null && !cards.isEmpty())
				? new ArrayList<>(cards)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Card.class));
		return CollectionModel.of(content, link);
	}

	@GetMapping("/{id}/addresses")
	@Operation(summary = "Return all addresses for the specified customer identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if the retrieval is successful")
	})
	public Object getCustomerAddresses(
			@Parameter(description = "Customer identifier") @PathVariable("id") String id) {
		final User user = this.userService.getUser(id);
		final Link link = linkTo(methodOn(CustomerController.class)
				.getCustomerAddresses(user.getUsername())).withSelfRel();

		final AddressController addressController = methodOn(AddressController.class);

		final List<EntityModel<Address>> addresses = user.getAddresses()
				.stream().map(address -> EntityModel.of(address).add(linkTo(addressController.getAddress(address.getAddressId())).withSelfRel()))
				.collect(Collectors.toList());
		final Iterable<Object> content = (addresses != null && !addresses.isEmpty())
				? new ArrayList<>(addresses)
				: Collections.singletonList(new EmbeddedWrappers(false).emptyCollectionOf(Address.class));
		return CollectionModel.of(content, link);
	}
}
