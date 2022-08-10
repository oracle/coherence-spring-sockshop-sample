/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.support.IdStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.model.Card;
import com.oracle.coherence.examples.sockshop.spring.users.model.CardId;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(
		path = "/cards",
		consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE},
		produces = { MediaTypes.HAL_JSON_VALUE, MediaType.ALL_VALUE})
@Slf4j
public class CardController {

	@Autowired
	private UserService userService;

	@GetMapping
	@Operation(summary = "Return all cards associated with a user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if retrieval is successful")
	})
	//@NewSpan
	public CollectionModel<Object> getAllCards() {
		final Link link = linkTo(methodOn(CardController.class)
				.getAllCards()).withSelfRel();
		EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
		EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(Card.class);
		final CollectionModel<Object> result = CollectionModel.of(Collections.singletonList(wrapper), link); //TODO
		return result;
	}

	@PostMapping
	@Operation(summary = "Register a credit card for a user; no-op if the card exist")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if card is successfully registered")
	})
	//@NewSpan
	public IdStatusResponse registerCard(@Parameter(description = "Add card request") @RequestBody AddCardRequest req) {
		final Card card = new Card(req.longNum, req.expires, req.ccv);
		final CardId id = this.userService.addCard(req.userID, card);
		return new IdStatusResponse(id.toString());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Return card for the specified identifier")
	//@NewSpan
	public EntityModel<Card> getCard(@Parameter(description = "Card identifier") @PathVariable("id") CardId id) {
		final Card card = this.userService.getCard(id).mask();
		final CardController cardController = methodOn(CardController.class);
		return EntityModel.of(card).add(linkTo(cardController.getCard(id)).withSelfRel());
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete card for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if card is successfully deleted")
	})
	@NewSpan
	public BooleanStatusResponse deleteCard(@Parameter(description = "Card identifier") @PathVariable("id")CardId id) {
		BooleanStatusResponse booleanStatusResponse;
		try {
			this.userService.removeCard(id);
			booleanStatusResponse = new BooleanStatusResponse(true);
		}
		catch (RuntimeException e) {
			log.error("Error deleting card", e);
			booleanStatusResponse = new BooleanStatusResponse(false);
		}
		return booleanStatusResponse;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddCardRequest {
		public String longNum;
		public String expires;
		public String ccv;
		public String userID;
	}
}
