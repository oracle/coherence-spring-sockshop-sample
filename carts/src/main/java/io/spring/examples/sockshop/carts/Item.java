/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.carts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Representation of a single item in a shopping cart.
 */
@Data
@Schema(description = "Shopping cart item")
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
     * The cart this item belongs to, purely for JPA optimization.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Cart cart;

    /**
     * Default constructor.
     */
    public Item() {
    }

    /**
     * Construct an Item instance.
     *
     * @param itemId    the item identifier
     * @param quantity  the item quantity
     * @param unitPrice the item's price per unit
     */
    public Item(String itemId, int quantity, float unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Copy constructor.
     * <p/>
     * This constructor intentionally does not copy {@code carts} field,
     * in order to detach the entity from the persistence context when
     * merging carts.
     *
     * @param item the item to initialize this item from
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Item(Item item) {
        this (item.itemId, item.quantity, item.unitPrice);
    }

    /**
     * Return the cart this item belongs to.
     *
     * @return the cart this item belongs to
     */
    Cart getCart() {
        return cart;
    }

    /**
     * Set the cart this item belongs to.
     *
     * @param cart the cart to set
     *
     * @return this item
     */
    Item setCart(Cart cart) {
        this.cart = cart;
        return this;
    }

    /**
     * Set the item quantity.
     *
     * @param quantity the new quantity
     *
     * @return this item
     */
    public Item setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    /**
     * Increment quantity in the cart by the specified number of items.
     *
     * @param count the number of items to increase quantity by
     *
     * @return this item
     */
    public Item incrementQuantity(int count) {
        this.quantity += count;
        return this;
    }
}
