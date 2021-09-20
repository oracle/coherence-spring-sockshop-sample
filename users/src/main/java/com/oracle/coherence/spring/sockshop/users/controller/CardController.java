/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.controller.support.BooleanStatusResponse;
import com.oracle.coherence.spring.sockshop.users.controller.support.IdStatusResponse;
import com.oracle.coherence.spring.sockshop.users.model.Card;
import com.oracle.coherence.spring.sockshop.users.model.CardId;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/cards")
public class CardController {

	@Autowired
	private UserService userService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return all cards associated with a user")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if retrieval is successful")
	})
	//@NewSpan
	public ResponseEntity<CollectionModel<Object>> getAllCards() {
		final Link link = linkTo(methodOn(CardController.class)
				.getAllCards()).withSelfRel();
		EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
		EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(Card.class);
		final CollectionModel<Object> result = CollectionModel.of(Collections.singletonList(wrapper), link); //TODO
		return ResponseEntity.ok(result);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Register a credit card for a user; no-op if the card exist")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if card is successfully registered")
	})
	//@NewSpan
	public ResponseEntity registerCard(@Parameter(description = "Add card request") @RequestBody AddCardRequest req) {
		final Card card = new Card(req.longNum, req.expires, req.ccv);
		final CardId id = this.userService.addCard(req.userID, card);
		return ResponseEntity.ok(new IdStatusResponse(id.toString()));
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Return card for the specified identifier")
	//@NewSpan
	public Card getCard(@Parameter(description = "Card identifier") @PathVariable("id") CardId id) {
		return this.userService.getCard(id).mask();
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete card for the specified identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if card is successfully deleted")
	})
	//@NewSpan
	public ResponseEntity deleteCard(@Parameter(description = "Card identifier") @PathVariable("id")CardId id) {
		try {
			this.userService.removeCard(id);
			return ResponseEntity.ok(new BooleanStatusResponse(true));
		}
		catch (RuntimeException e) {
			return ResponseEntity.ok(new BooleanStatusResponse(false));
		}
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
