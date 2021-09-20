/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.carts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Implementation of the Cart Service REST API.
 */
@Scope(SCOPE_SINGLETON)
@RestController
@RequestMapping("/carts")
public class CartResource implements CartApi {

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
