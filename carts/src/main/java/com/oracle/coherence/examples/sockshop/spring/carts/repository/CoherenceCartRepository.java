/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.repository;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Cart;
import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * An implementation of {@link CartRepository}
 * that that uses Coherence as a backend data store.
 */
@Component
public class CoherenceCartRepository implements CartRepository {
    protected final NamedMap<String, Cart> carts;

    @Autowired
    CoherenceCartRepository(@CoherenceMap("carts") NamedMap<String, Cart> carts) {
        this.carts = carts;
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        return carts.computeIfAbsent(customerId, v -> new Cart(customerId));
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        final Cart source = carts.remove(sourceId);
        if (source == null) {
            return false;
        }

        carts.invoke(targetId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            entry.setValue(cart.merge(source));
            return null;
        });

        return true;
    }

    @Override
    public boolean deleteCart(String customerId) {
        return null != carts.remove(customerId);
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        return getOrCreateCart(cartId).getItem(itemId);
    }

    @Override
    public List<Item> getItems(String cartId) {
        return getOrCreateCart(cartId).getItems();
    }

    @Override
    public Item addItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.add(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public Item updateItem(String cartId, Item item) {
        return carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            Item newItem = cart.update(item);
            entry.setValue(cart);
            return newItem;
        });
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        carts.invoke(cartId, entry -> {
            Cart cart = entry.getValue(new Cart(entry.getKey()));
            cart.remove(itemId);
            entry.setValue(cart);
            return null;
        });
    }
}
