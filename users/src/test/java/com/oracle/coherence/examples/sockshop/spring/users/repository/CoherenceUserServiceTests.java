/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.repository;

import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import jakarta.inject.Inject;


/**
 * Tests for Coherence repository implementation.
 */
@SpringBootTest
@DirtiesContext
class CoherenceUserServiceTests extends UserServiceTests {

	@Inject
	ApplicationContext context;

	@Override
	public UserService getUserRepository() {
		return context.getBean(UserService.class);
	}
}