/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * REST API for {@code /catalog} service.
 */
public interface CatalogApi {
    @GetMapping
    @Operation(summary = "Return the socks that match the specified query parameters")
    Collection<? extends Sock> getSocks(@Parameter(description = "tag identifiers")
           @RequestParam(value = "tags", required = false) String tags,
           @Parameter(name = "order", description = "order identifier")
           @RequestParam(value = "order", required = false, defaultValue = "price") String order,
           @Parameter(description = "page number")
           @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum,
           @Parameter(description = "page size")
           @RequestParam(value = "size", required = false, defaultValue = "10")  int pageSize);

    @GetMapping("size")
    @Operation(summary = "Return sock count for the specified tag identifiers")
    CatalogResource.Count getSockCount(@Parameter(description = "tag identifiers")
                                       @RequestParam(value = "tags", required = false) String tags);

	@GetMapping("{id}")
	@Operation(summary = "Return socks for the specified sock identifier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if socks are found"),
			@ApiResponse(responseCode = "404", description = "if socks do not exist")
	})
	ResponseEntity<Sock> getSock(@Parameter(description = "sock identifier")
								 @PathVariable("id") String sockId);

	@GetMapping(path = "images/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
	@Operation(summary = "Return the sock images for the specified image identifer")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "if image is found"),
			@ApiResponse(responseCode = "404", description = "if image does not exist")
	})
	ResponseEntity<Resource> getImage(@Parameter(description = "image identifier")
									  @PathVariable("image") String image);
}
