/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.repository;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for Coherence repository implementation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "coherence.localhost=127.0.0.1",
                "coherence.ttl=0",
                "java.net.preferIPv4Stack=true",
                "coherence.wka=127.0.0.1"
        }
)
class CoherenceCartRepositoryAsyncIT extends CartRepositoryTest {

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }

    @Autowired
    void setCartRepository(CartRepositoryAsync cartRepository) {
        carts = new SyncCartRepository(cartRepository);
    }
}
