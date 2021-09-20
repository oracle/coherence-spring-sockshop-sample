/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URI;

/**
 * The incoming request for new orders.
 */
@ToString
@NoArgsConstructor
public class NewOrderRequest {
    /**
     * The URI that should be used to fetch customer information.
     */
    @Schema(description = "The URI that should be used to fetch customer information")
    public URI customer;

    /**
     * The URI that should be used to fetch billing/shipping address information.
     */
    @Schema(description = "The URI that should be used to fetch billing/shipping address information")
    public URI address;

    /**
     * The URI that should be used to fetch payment card information.
     */
    @Schema(description = "The URI that should be used to fetch payment card information")
    public URI card;

    /**
     * The URI that should be used to fetch order items from the shopping cart.
     */
    @Schema(description = "The URI that should be used to fetch order items from the shopping cart")
    public URI items;

    @Builder
    NewOrderRequest(URI customer, URI address, URI card, URI items) {
        this.customer = customer;
        this.address = address;
        this.card = card;
        this.items = items;
    }
}