/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.service;

import com.oracle.coherence.spring.sockshop.users.model.*;
import com.oracle.coherence.spring.sockshop.users.repository.CoherenceUserRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
//@Traced
public class DefaultUserService implements UserService {

	private CoherenceUserRepository userRepository;

	public DefaultUserService(CoherenceUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public AddressId addAddress(String userID, Address address) {
		return this.userRepository.update(userID, User::addAddress, address, User::new).getId();
	}

	@Override
	public Address getAddress(AddressId id) {
		return getOrCreate(id.getUser()).getAddress(id.getAddressId());
	}

	@Override
	public void removeAddress(AddressId id) {
		String userID = id.getUser();
		this.userRepository.update(userID, User::removeAddress, id.getAddressId(), User::new);
	}

	@Override
	public CardId addCard(String userID, Card card) {
		return this.userRepository.update(userID, User::addCard, card, User::new).getId();
	}

	@Override
	public Card getCard(CardId id) {
		return this.getOrCreate(id.getUser()).getCard(id.getCardId());
	}

	@Override
	public void removeCard(CardId id) {
		String userId = id.getUser();
		this.userRepository.update(userId, User::removeCard, id.getCardId(), User::new);
	}

	@Override
	public Collection<User> getAllUsers() {
		return Streamable.of(this.userRepository.findAll()).toList();
	}

	@Override
	public User getOrCreate(String id) {
		return this.userRepository.getMap().getOrDefault(id, new User(id));
	}

	@Override
	public User getUser(String id) {
		return this.userRepository.findById(id).orElseGet(() -> null);
	}

	@Override
	public User removeUser(String id) {
		final User user = this.userRepository.findById(id).orElseGet(() -> null);

		if (user != null) {
			this.userRepository.delete(user);
		}
		return user;
	}

	@Override
	public boolean authenticate(String username, String password) {
		return this.userRepository.getMap().invoke(username, entry -> {
			User u = entry.getValue(new User(entry.getKey()));
			return u.authenticate(password);
		});
	}

	@Override
	public User register(User user) {
		return this.userRepository.getMap().putIfAbsent(user.getUsername(), user);
	}
}
