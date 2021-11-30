/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Customer information.
 */
@Data
@NoArgsConstructor
@Schema(description = "Customer information")
public class Customer implements Serializable {
    /**
     * First name.
     */
    @Schema(description = "First name")
    private String firstName;

    /**
     * Last name.
     */
    @Schema(description = "Last name")
    private String lastName;

    @Builder
    Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
