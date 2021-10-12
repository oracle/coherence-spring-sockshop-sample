/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Shipping or billing address.
 */
@Data
@NoArgsConstructor
@Schema(description = "Shipping or billing address")
public class Address implements Serializable {
    /**
     * Street number.
     */
    @Schema(description = "Street number")
    private String number;

    /**
     * Street name.
     */
    @Schema(description = "Street name")
    private String street;

    /**
     * City name.
     */
    @Schema(description = "City name")
    private String city;

    /**
     * Postal code.
     */
    @Schema(description = "Postal code")
    private String postcode;

    /**
     * Country name.
     */
    @Schema(description = "Country name")
    private String country;

    @Builder
    Address(String number, String street, String city, String postcode, String country) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }
}
