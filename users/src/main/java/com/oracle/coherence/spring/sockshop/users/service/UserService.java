/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.service;

import com.oracle.coherence.spring.sockshop.users.model.*;

import java.util.Collection;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface UserService {
	/**
	 * Add an {@code Address} to the specified user.
	 *
	 * @param userID  the user id
	 * @param address the address to add
	 *
	 * @return the {@code AddressId} of the address
	 */
	AddressId addAddress(String userID, Address address);

	/**
	 * Return the {@code Address} for the specified address identifier.
	 *
	 * @param id the address id
	 *
	 * @return the {@code Address} with the specified identifier
	 */
	 Address getAddress(AddressId id);

	/**
	 * Remove the address with the specified identifier.
	 *
	 * @param id the address id
	 */
	void removeAddress(AddressId id);

	/**
	 * Add a {@code Card} to the specified user.
	 *
	 * @param userID the user id
	 * @param card   the card to add
	 *
	 * @return the {@code CardId} of the card
	 */
	CardId addCard(String userID, Card card);

	/**
	 * Return the {@code Card} with the specified card identifier.
	 *
	 * @param id the card id
	 *
	 * @return the {@code Card} with the specified card id
	 */
	Card getCard(CardId id);

	/**
	 * Remove the card  with the specified identifier.
	 *
	 * @param id the card id
	 */
	void removeCard(CardId id);

	/**
	 * Return all users.
	 *
	 * @return a collection of {@code User}s
	 */
	Collection<User> getAllUsers();

	/**
	 * Return an existing {@code User} for the specified user identifier;
	 * or a newly created {@code User}.
	 *
	 * @param id the user id
	 *
	 * @return the {@code User} with the specified user id
	 */
	User getOrCreate(String id);

	/**
	 * Return the {@code User} with the specified user identifier.
	 *
	 * @param id the user id
	 *
	 * @return the {@code User} with the specified user id
	 */
	 User getUser(String id);

	/**
	 * Remove the {@code User} with the specified user identifier;
	 *
	 * @param id the id
	 *
	 * @return the removed {@code User}; can be {@code null}
	 */
	User removeUser(String id);

	/**
	 * Authenticate a {@code User} with the specified username against
	 * the specified password.
	 *
	 * @param username the username of the user to be authenticated
	 * @param password the password to authenticate against
	 *
	 * @return true if password match
	 */
	boolean authenticate(String username, String password);

	/**
	 * Register the specified user.
	 *
	 * @param user the user to be registered
	 *
	 * @return the registered user
	 */
	User register(User user);
}
