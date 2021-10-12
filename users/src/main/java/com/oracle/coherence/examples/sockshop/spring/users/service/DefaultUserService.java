/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.service;

import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.AddressId;
import com.oracle.coherence.examples.sockshop.spring.users.model.Card;
import com.oracle.coherence.examples.sockshop.spring.users.model.CardId;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.repository.CoherenceUserRepository;
import com.oracle.coherence.examples.sockshop.spring.users.controller.support.exceptions.CardNotFoundException;
import org.springframework.data.util.Streamable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
//@Traced
public class DefaultUserService implements UserService {

	private final CoherenceUserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public DefaultUserService(CoherenceUserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public AddressId addAddress(String userID, Address address) {
		return this.userRepository.update(userID, User::addAddress, address, User::new).getAddressId();
	}

	@Override
	public Address getAddress(AddressId id) {
		final User user = this.getUser(id.getUser());

		if (user == null) {
			return null;
		}

		return user.getAddress(id);
	}

	@Override
	public void removeAddress(AddressId id) {
		String userID = id.getUser();
		this.userRepository.update(userID, User::removeAddress, id.getAddressId(), User::new);
	}

	@Override
	public CardId addCard(String userID, Card card) {
		return this.userRepository.update(userID, User::addCard, card, User::new).getCardId();
	}

	@Override
	public Card getCard(CardId id) {
		final User user = this.getUser(id.getUser());
		if (user == null) {
			return null;
		}
		final Card card = user.getCard(id);

		if (card == null) {
			throw new CardNotFoundException();
		}
		return card;
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
	public User register(User user) {
		final User userToRegister = new User();
		userToRegister.setFirstName(user.getFirstName());
		userToRegister.setLastName(user.getLastName());
		userToRegister.setEmail(user.getEmail());
		userToRegister.setUsername(user.getUsername());
		userToRegister.setPassword(this.passwordEncoder.encode(user.getPassword()));

		if (user.getAddresses() != null) {
			userToRegister.setAddresses(user.getAddresses());
		}
		if (user.getCards() != null) {
			userToRegister.setCards(user.getCards());
		}

		return this.userRepository.getMap().putIfAbsent(userToRegister.getUsername(), userToRegister);
	}

}
