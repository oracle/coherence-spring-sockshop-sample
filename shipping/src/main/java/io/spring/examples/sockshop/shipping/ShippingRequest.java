/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.shipping;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Shipping request that is received from Order service.
 */
@Data
@NoArgsConstructor
@Schema(description = "Shipping request that is received from Order service")
public class ShippingRequest implements Serializable {
    /**
     * Order identifier.
     */
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Shipping address.
     */
    @Schema(description = "Shipping address")
    private Address address;

    /**
     * The number of items in the order.
     */
    @Schema(description = "The number of items in the order")
    private int itemCount;

    @Builder
    ShippingRequest(String orderId, Address address, int itemCount) {
        this.orderId = orderId;
        this.address = address;
        this.itemCount = itemCount;
    }
}
