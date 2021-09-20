/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.carts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Implementation of Items sub-resource REST API.
 */
@RestController
@RequestMapping("/carts-async/{customerId}/items")
public class ItemsResourceAsync implements ItemsApiAsync {

	@Autowired
    private CartRepositoryAsync carts;


    @Override
    public CompletionStage<List<Item>> getItems(String cartId) {
        return carts.getItems(cartId);
    }

    @Override
    public CompletionStage<ResponseEntity<Item>> addItem(String cartId, Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }

        return carts.addItem(cartId, item)
                    .thenApply(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    @Override
    public CompletionStage<ResponseEntity<Item>> getItem(String cartId, String itemId) {
        return carts.getItem(cartId, itemId)
                    .thenApply(item ->
                            item == null
                            ? ResponseEntity.notFound().build()
                            : ResponseEntity.ok(item));
    }

    @Override
    public CompletionStage<ResponseEntity<Void>> deleteItem(String cartId, String itemId) {
        return carts.deleteItem(cartId, itemId)
                .thenApply(ignore -> ResponseEntity.accepted().build());
    }

    @Override
    public CompletionStage<ResponseEntity<Void>> updateItem(String cartId, Item item) {
        return carts.updateItem(cartId, item)
                .thenApply(ignore -> ResponseEntity.accepted().build());
    }
}
