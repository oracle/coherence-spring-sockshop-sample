/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepository;
import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Implementation of Items sub-resource REST API.
 */
@RestController
@RequestMapping(path="/carts/{customerId}/items",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemsController {

	private final CartRepository carts;

    public ItemsController(CartRepository carts) {
        this.carts = carts;
    }

    @GetMapping
    @Operation(summary = "Return the list of products in the customer's shopping cart")
    @ApiResponse(responseCode = "200", description = "The list of products in the customer's shopping cart",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = Item.class))))
    public List<Item> getItems(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId) {
        return carts.getItems(customerId);
    }

    @PostMapping
    @Operation(summary = "Add item to the shopping cart",
               description = "This operation will add item to the shopping cart if it doesn't already exist, or " +
                             "increment quantity by the specified number of items if it does")
    @ApiResponse(responseCode = "201", description = "Added item",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Item.class)))
    public ResponseEntity<Item> addItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Item to add to the cart")
            @RequestBody Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }
        Item result = carts.addItem(customerId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("{itemId}")
    @Operation(summary = "Return specified item from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "If specified item exists in the cart",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "404", description = "If specified item does not exist in the cart")})
    public ResponseEntity<Item> getItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId,
            @Parameter(name = "itemId", description = "Item identifier")
            @PathVariable("itemId") String itemId) {
        Item item = carts.getItem(customerId, itemId);
        return item == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(item);
    }

    @DeleteMapping("{itemId}")
    @Operation(summary = "Remove specified item from the shopping cart, if it exists")
    @ApiResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    public ResponseEntity<Void> deleteItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId,
            @Parameter(name = "itemId", description = "Item identifier")
            @PathVariable("itemId") String itemId) {
        carts.deleteItem(customerId, itemId);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping
    @Operation(summary = "Update item in a shopping cart",
               description = "This operation will add item to the shopping cart if it doesn't already exist, or " +
                             "replace it with the specified item if it does")
    @ApiResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    public ResponseEntity<Void> updateItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Item to update")
            @RequestBody Item item) {
        carts.updateItem(customerId, item);
        return ResponseEntity.accepted().build();
    }
}
