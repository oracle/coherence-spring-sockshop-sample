/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Representation of a single order item.
 */
@Data
@NoArgsConstructor
public class Item implements Serializable {
    /**
     * The item identifier.
     */
    @Schema(description = "The item identifier")
    private String itemId;

    /**
     * The item quantity.
     */
    @Schema(description = "The item quantity")
    private int quantity;

    /**
     * The item's price per unit.
     */
    @Schema(description = "The item's price per unit")
    private float unitPrice;

    /**
     * The order this item belongs to, for JPA optimization.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@JsonIgnore
    private Order order;

    @Builder
    Item(String itemId, int quantity, float unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
