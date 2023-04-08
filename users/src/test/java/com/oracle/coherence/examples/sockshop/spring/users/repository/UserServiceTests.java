/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.repository;

import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.AddressId;
import com.oracle.coherence.examples.sockshop.spring.users.model.Card;
import com.oracle.coherence.examples.sockshop.spring.users.model.CardId;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.inject.Inject;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Abstract base class containing tests for all
 * {@link UserService} implementations.
 */
public abstract class UserServiceTests {
	private UserService users;

	protected abstract UserService getUserRepository();

	@Inject
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		users = getUserRepository();
		users.removeUser("testuser");
	}

	@Test
	void testUserCreation() {
		User user = users.getOrCreate("testuser");
		user.setLastName("test");
		user.setPassword("testpassword");

		users.register(user);

		assertThat(users.getUser("testuser").getLastName(), is("test"));
	}

	@Test
	void testAddAddress() {
		final User user = users.getOrCreate("testuser");
		user.setPassword("testpassword");
		users.register(user);

		AddressId addressId = users.addAddress(user.getUsername(), new Address("555", "woodbury St", "Westford", "01886", "USA"));
		assertThat(users.getAddress(addressId).getCity(), is("Westford"));
	}

	@Test
	void testAddCard() {
		final User user = users.getOrCreate("testuser");
		user.setPassword("testpassword");
		users.register(user);

		CardId cardId = users.addCard(user.getUsername(), new Card("1234123412341234", "12/19", "123"));
		assertThat(users.getCard(cardId).getLongNum(), is("1234123412341234"));
	}

	@Test
	void testUserDeletion() {
		User user = users.getOrCreate("testuser");
		user.setPassword("testpassword");

		users.register(user);

		assertThat(users.removeUser("testuser"), is(notNullValue()));
		assertThat(users.getUser("testuser"), is(nullValue()));
	}

	@Test
	void testAllUsers() {
		User u1 = new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass");
		User u2 = new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass");
		User u3 = new User("zar", "passzar", "zar@weavesocks.com", "zaruser", "pass");

		users.register(u1);
		users.register(u2);
		users.register(u3);

		Collection<? extends User> allUsers = users.getAllUsers();

		assertThat(allUsers.size(), greaterThanOrEqualTo(3));
	}
}
