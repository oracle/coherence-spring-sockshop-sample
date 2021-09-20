/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.service;

import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.repository.CoherenceUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CoherenceUserDetailsService implements UserDetailsService {

	@Autowired
	private CoherenceUserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		final User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
	}

}
