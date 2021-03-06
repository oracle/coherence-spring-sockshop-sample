/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartResourceAsyncIT extends CartControllerIT {

    protected String getBasePath() {
        return "/carts-async";
    }

    @Autowired
    void setCartRepository(CartRepositoryAsync cartRepository) {
        this.carts = new SyncCartRepository(cartRepository);
    }
}
