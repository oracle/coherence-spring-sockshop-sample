/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Representation of a credit card.
 */
@Data
@NoArgsConstructor
@Schema(description = "User credit card")
@Relation(collectionRelation = "card", itemRelation = "card")
public class Card extends RepresentationModel<Card> implements Serializable {
	/**
	 * The card identifier.
	 */
	@Schema(description = "Card identifier")
	private String cardId;

	/**
	 * The card number.
	 */
	@Schema(description = "Card number")
	private String longNum;

	/**
	 * The expiration date.
	 */
	@Schema(description = "Expiration date")
	private String expires;

	/**
	 * The security code.
	 */
	@Schema(description = "CCV code")
	private String ccv;

	/**
	 * The user this card belongs to, purely for JPA optimization.
	 */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private User user;

	/**
	 * Construct {@code Card} with specified parameters.
	 */
	public Card(String longNum, String expires, String ccv) {
		this.longNum = longNum;
		this.expires = expires;
		this.ccv = ccv;
	}

	/**
	 * Return the user this address belongs to.
	 *
	 * @return the user this address belongs to
	 */
	public User getUser() {
	return user;
	}

	/**
	 * Set the uer this address belongs to.
	 *
	 * @param user the user to set
	 *
	 * @return this card
	 */
	public Card setUser(User user) {
		this.user = user;
		return this;
	}

	/**
	 * Set the card id.
	 */
	public Card setCardId(String id) {
		this.cardId = id;
		return this;
	}

	/**
	 * Return CardId for this card.
	 */
	public CardId getId() {
		return new CardId(user.getUsername(), cardId);
	}

	/**
	 * Return the card with masked card number.
	 *
	 * @return the card with masked card number
	 */
	public Card mask() {
		if (longNum != null) {
			int len = longNum.length() - 4;
			longNum = "****************".substring(0, len) + longNum.substring(len);
		}
		return this;
	}

	/**
	 * Return the last 4 digit of the card number.
	 *
	 * @return the last 4 digit of the card number
	 */
	public String last4() {
		return longNum == null ? null : longNum.substring(longNum.length() - 4);
	}

	@Override
	public Links getLinks() {
		List<Link> links = com.oracle.coherence.spring.sockshop.users.model.Links.card(this.getId()).entrySet().stream()
				.map(entry -> Link.of(entry.getValue().href, entry.getKey()))
				.collect(Collectors.toList());
		return Links.of(links);
	}
}
