/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts;

import java.util.concurrent.CompletionStage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Async REST API for {@code /carts} service.
 */
public interface CartApiAsync {

    @GetMapping(value = "{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return customer's shopping cart")
    CompletionStage<Cart> getCart(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId);

    @DeleteMapping("{customerId}")
    @Operation(summary = "Delete customer's shopping cart")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "if the shopping cart was successfully deleted"),
        @ApiResponse(responseCode = "404", description = "if the shopping cart doesn't exist")
    })
    CompletionStage<ResponseEntity<Void>> deleteCart(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId);

    @GetMapping("{customerId}/merge")
    @Operation(summary = "Merge one shopping cart into another",
               description = "Customer can add products to a shopping cart anonymously, "
                       + "but when she logs in the anonymous shopping cart needs to be merged "
                       + "into the customer's own shopping cart")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "if the shopping carts were successfully merged"),
        @ApiResponse(responseCode = "404", description = "if the session shopping cart doesn't exist")
    })
    CompletionStage<ResponseEntity<Void>> mergeCarts(
            @Parameter(name = "customerId", description = "Customer identifier")
            @PathVariable("customerId") String customerId,
            @Parameter(name = "sessionId", required = true, description = "Anonymous session identifier")
            @RequestParam("sessionId") String sessionId);
}
