/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shipment information received from the Shipping service.
 */
@Data
@NoArgsConstructor
public class Shipment implements Serializable {
    /**
     * Shipping carrier.
     */
    private String carrier;

    /**
     * Tracking number.
     */
    private String trackingNumber;

    /**
     * Estimated delivery date.
     */
    private LocalDate deliveryDate;

    @Builder
    Shipment(String carrier, String trackingNumber, LocalDate deliveryDate) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.deliveryDate = deliveryDate;
    }
}
