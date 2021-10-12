/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Composite key for the {@link Card class} when using JPA.
 */
@Data
public class CardId implements Serializable {
	/**
	 * The ID of the customer to whom the card belongs.
	 */
	private String user;

	/**
	 * The card id.
	 */
	private String cardId;

	/**
	 * Default constructor.
	 */
	public CardId() {}

	@Builder
	public CardId(String id) {
		String[] parts = id.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Card Id is in the wrong format");
		}
		user = parts[0];
		cardId = parts[1];
	}

	/**
	 * Construct {@code CardId} with specified parameters.
	 */
	public CardId(String customerId, String cardId) {
		this.user = customerId;
		this.cardId = cardId;
	}

	@Override
	public String toString() {
		return user + ":" + cardId;
	}

}
