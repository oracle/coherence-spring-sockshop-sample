/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.repository;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Cart;
import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import com.oracle.coherence.spring.annotation.Name;
import com.tangosol.net.AsyncNamedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * An implementation of {@link CartRepository} that that uses Coherence as a backend data
 * store.
 */
@Component
public class CoherenceCartRepositoryAsync implements CartRepositoryAsync {
    protected final AsyncNamedMap<String, Cart> carts;

    @Autowired
    CoherenceCartRepositoryAsync(@Name("carts") AsyncNamedMap<String, Cart> carts) {
        this.carts = carts;
    }

    @Override
    public CompletionStage<Boolean> deleteCart(String customerId) {
        return carts.remove(customerId).thenApply(Objects::nonNull);
    }

    @Override
    public CompletionStage<Boolean> mergeCarts(String targetId, String sourceId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        carts.remove(sourceId)
                .whenComplete((source, t1) -> {
                    if (t1 != null) {
                        future.completeExceptionally(t1);
                    } else {
                        if (source == null) {
                            future.complete(false);
                        } else {
                            carts.invoke(targetId, entry -> {
                                Cart cart = entry.getValue(new Cart(entry.getKey()));
                                entry.setValue(cart.merge(source));
                                return null;
                            }).whenComplete((target, t2) -> {
                                if (t2 != null) {
                                    future.completeExceptionally(t2);
                                } else {
                                    future.complete(true);
                                }
                            });
                        }
                    }
                });

        return future;
    }

    @Override
    public CompletionStage<Item> getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).thenApply(cart -> cart.getItem(itemId));
    }

    @Override
    public CompletionStage<List<Item>> getItems(String cartId) {
        return getOrCreateCart(cartId).thenApply(Cart::getItems);
    }

    @Override
    public CompletionStage<Item> addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Item> updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public CompletionStage<Void> deleteItem(String cartId, String itemId) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        }).thenAccept(cart -> {});
    }

    @Override
    public CompletionStage<Cart> getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }
}
