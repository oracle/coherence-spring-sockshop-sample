/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepository;
import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Implementation of Items sub-resource REST API.
 */
@Scope(SCOPE_SINGLETON)
@RestController
@RequestMapping("/carts/{customerId}/items")
public class ItemsResource implements ItemsApi {

	@Autowired
    private CartRepository carts;

    @Override
    public List<Item> getItems(String customerId) {
        return carts.getItems(customerId);
    }

    @Override
    public ResponseEntity<Item> addItem(String customerId, Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }
        Item result = carts.addItem(customerId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<Item> getItem(String customerId, String itemId) {
        Item item = carts.getItem(customerId, itemId);
        return item == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(item);
    }

    @Override
    public ResponseEntity<Void> deleteItem(String customerId, String itemId) {
        carts.deleteItem(customerId, itemId);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> updateItem(String customerId, Item item) {
        carts.updateItem(customerId, item);
        return ResponseEntity.accepted().build();
    }
}
