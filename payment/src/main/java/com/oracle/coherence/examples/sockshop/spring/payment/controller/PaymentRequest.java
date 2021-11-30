/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.controller;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Address;
import com.oracle.coherence.examples.sockshop.spring.payment.model.Card;
import com.oracle.coherence.examples.sockshop.spring.payment.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment request that is received from Orders service for authorization.
 */
@Data
@NoArgsConstructor
@Schema(description = "Payment request that is received from Orders service for authorization")
public class PaymentRequest {
    /**
     * Order identifier.
     */
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Customer information.
     */
    @Schema(description = "Customer information")
    private Customer customer;

    /**
     * Billing address.
     */
    @Schema(description = "Billing address")
    private Address address;

    /**
     * Payment card details.
     */
    @Schema(description = "Payment card details")
    private Card card;

    /**
     * Payment amount.
     */
    @Schema(description = "Payment amount")
    private float amount;

    @Builder
    PaymentRequest(String orderId, Customer customer, Address address, Card card, float amount) {
        this.orderId = orderId;
        this.customer = customer;
        this.address = address;
        this.card = card;
        this.amount = amount;
    }
}
