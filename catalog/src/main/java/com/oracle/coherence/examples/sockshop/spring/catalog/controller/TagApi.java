/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * REST API for {@code /tags} service.
 */
public interface TagApi {
    @GetMapping
    @Operation(summary = "Return all tags")
    TagsResource.Tags getTags();
}
