/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Cart;
import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepositoryAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletionStage;

/**
 * Implementation of the Cart Service REST API.
 */
@RequestMapping("/carts-async")
@RestController
public class CartControllerAsync implements CartApiAsync {

    @Autowired
    private CartRepositoryAsync carts;

    @Override
    public CompletionStage<Cart> getCart(String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @Override
    public CompletionStage<ResponseEntity<Void>> deleteCart(String customerId) {
        return carts.deleteCart(customerId)
                .thenApply(deleted -> deleted
                        ? ResponseEntity.accepted().build()
                        : ResponseEntity.notFound().build());
    }

    @Override
    public CompletionStage<ResponseEntity<Void>> mergeCarts(String customerId, String sessionId) {
        return carts.mergeCarts(customerId, sessionId)
                .thenApply(fMerged -> fMerged
                        ? ResponseEntity.accepted().build()
                        : ResponseEntity.notFound().build());
    }
}
