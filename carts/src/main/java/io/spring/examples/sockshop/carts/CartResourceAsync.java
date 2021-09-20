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

import java.util.concurrent.CompletionStage;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Implementation of the Cart Service REST API.
 */
@Scope(SCOPE_SINGLETON)
@RequestMapping("/carts-async")
@RestController
public class CartResourceAsync implements CartApiAsync {

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
