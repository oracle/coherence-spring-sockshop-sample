/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.repository;

import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.spring.data.config.CoherenceMap;
import com.oracle.coherence.spring.data.repository.CoherenceRepository;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;

/**
 * An implementation of {@link UserService}
 * that that uses Coherence as a backend data store.
 */
@CoherenceMap("users")
public interface CoherenceUserRepository extends CoherenceRepository<User, String> {
}
