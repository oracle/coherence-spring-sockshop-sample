/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Cart;
import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation of the Cart Service REST API.
 */
@RestController
@RequestMapping("/carts")
public class CartController implements CartApi {

	@Autowired
    private CartRepository carts;

    @Override
    public Cart getCart(String customerId) {
        return carts.getOrCreateCart(customerId);
    }

    @Override
    public ResponseEntity<Void> deleteCart(String customerId) {
        return carts.deleteCart(customerId)
                ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> mergeCarts(String customerId, String sessionId) {
        boolean fMerged = carts.mergeCarts(customerId, sessionId);
        return fMerged
                ? ResponseEntity.accepted().build()
                : ResponseEntity.notFound().build();
    }
}
