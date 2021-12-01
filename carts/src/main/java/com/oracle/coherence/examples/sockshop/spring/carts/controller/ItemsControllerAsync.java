/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepositoryAsync;
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
import java.util.concurrent.CompletionStage;

/**
 * Implementation of Items sub-resource REST API.
 */
@RestController
@RequestMapping("/carts-async/{customerId}/items")
public class ItemsControllerAsync {

	private final CartRepositoryAsync carts;

    public ItemsControllerAsync(CartRepositoryAsync carts) {
        this.carts = carts;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return the list of products in the customer's shopping cart")
    @ApiResponse(
            responseCode = "200",
            description = "The list of products in the customer's shopping cart",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Item.class))
            ))
    public CompletionStage<List<Item>> getItems(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String cartId) {
        return carts.getItems(cartId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add item to the shopping cart",
            description = "This operation will add item to the shopping cart if it "
                    + "doesn't already exist, or increment quantity by the specified "
                    + "number of items if it does")
    @ApiResponse(responseCode = "201",
            description = "Added item",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Item.class)))
    public CompletionStage<ResponseEntity<Item>> addItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String cartId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Item to add to the cart")
            @RequestBody Item item) {
        if (item.getQuantity() == 0) {
            item.setQuantity(1);
        }

        return carts.addItem(cartId, item)
                    .thenApply(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    @GetMapping(value = "{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return specified item from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "If specified item exists in the cart",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "404",
                    description = "If specified item does not exist in the cart")
    })
    public CompletionStage<ResponseEntity<Item>> getItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String cartId,
            @Parameter(name = "itemId", description = "Item identifier")
            @PathVariable("itemId") String itemId) {
        return carts.getItem(cartId, itemId)
                    .thenApply(item ->
                            item == null
                            ? ResponseEntity.notFound().build()
                            : ResponseEntity.ok(item));
    }

    @DeleteMapping("{itemId}")
    @Operation(summary = "Remove specified item from the shopping cart, if it exists")
    @ApiResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    public CompletionStage<ResponseEntity<Void>> deleteItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String cartId,
            @Parameter(name = "itemId", description = "Item identifier")
            @PathVariable("itemId") String itemId) {
        return carts.deleteItem(cartId, itemId)
                .thenApply(ignore -> ResponseEntity.accepted().build());
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update item in a shopping cart",
            description = "This operation will add item to the shopping cart if it "
                    + "doesn't already exist, or replace it with the specified item "
                    + "if it does")
    @ApiResponse(responseCode = "202", description = "Regardless of whether the specified item exists in the cart")
    public CompletionStage<ResponseEntity<Void>> updateItem(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String cartId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Item to update")
            @RequestBody Item item) {
        return carts.updateItem(cartId, item)
                .thenApply(ignore -> ResponseEntity.accepted().build());
    }
}
