/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.repository;


import com.oracle.coherence.examples.sockshop.spring.carts.controller.CartControllerIT;
import com.oracle.coherence.examples.sockshop.spring.carts.controller.CartControllerAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for {@link CartControllerAsync}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "coherence.localhost=127.0.0.1",
                "coherence.ttl=0",
                "java.net.preferIPv4Stack=true",
                "coherence.wka=127.0.0.1"
        }
)
public class CartResourceAsyncIT extends CartControllerIT {

    protected String getBasePath() {
        return "/carts-async";
    }

    @Autowired
    void setCartRepository(CartRepositoryAsync cartRepository) {
        this.carts = new SyncCartRepository(cartRepository);
    }
}
