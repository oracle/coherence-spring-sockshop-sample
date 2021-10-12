/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests for {@link User} class.
 */
class UserTests {
	@Test
	void testUserCreation() {
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		assertThat(user.getUsername(), is("user"));
		assertThat(user.getAddresses(), is(empty()));
		assertThat(user.getCards(), is(empty()));
	}

	@Test
	void testAddAddress() {
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		Address address = user.addAddress(new Address("666", "Sock St", "Weave", "33633", "USA"));
		assertThat(user.getAddresses().size(), is(1));
		assertThat(user.getAddress(address.getAddressId()), is(address));
		assertThat(address.getUser(), is(user));
		assertThat(address.getCity(), is("Weave"));
		assertThat(address.getPostcode(), is("33633"));

		user.addAddress(new Address("777", "Sock St", "Weave", "33633", "USA"));
		assertThat(user.getAddresses().size(), is(2));
	}

	@Test
	void testRemoveAddress() {
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		Address address = user.addAddress(new Address("666", "Sock St", "Weave", "33633", "USA"));
		assertThat(user.getAddresses().size(), is(1));
		user.removeAddress(address.getAddressId().toString());
		assertThat(user.getAddresses().size(), is(0));
	}

	@Test
	void testAddCard() {
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		Card card = user.addCard(new Card("6854657645765476", "03/22", "456"));
		assertThat(user.getCards().size(), is(1));
		assertThat(user.getCard(card.getCardId()), is(card));
		assertThat(card.getUser(), is(user));
		assertThat(card.getLongNum(), is("6854657645765476"));
		assertThat(card.getCcv(), is("456"));

		user.addCard(new Card("7854657645765476", "06/25", "656"));
		assertThat(user.getCards().size(), is(2));
	}

	@Test
	void testRemoveCard() {
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		Card card = user.addCard(new Card("6854657645765476", "03/22", "456"));
		assertThat(user.getCards().size(), is(1));
		user.removeCard(card.getCardId().toString());
		assertThat(user.getCards().size(), is(0));
	}
}
