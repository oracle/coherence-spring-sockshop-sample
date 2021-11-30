/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Sock implements Serializable {
    /**
     * Product identifier.
     */
    @Schema(description = "Product identifier")
    private String id;

    /**
     * Product name.
     */
    @Schema(description = "Product name")
    private String name;

    /**
     * Product description.
     */
    @Schema(description = "Product description")
    private String description;

    /**
     * A list of product image URLs.
     */
    @Schema(description = "A list of product image URLs")
    private List<String> imageUrl;

    /**
     * Product price.
     */
    @Schema(description = "Product price")
    private float price;

    /**
     * Product count.
     */
    @Schema(description = "Product count")
    private int count;

    /**
     * Product tags.
     */
    @Schema(description = "Product tags")
    private Set<String> tag;
}
