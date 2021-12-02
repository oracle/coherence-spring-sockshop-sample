/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.controller;

import com.oracle.coherence.examples.sockshop.spring.catalog.repository.CatalogRepository;
import com.oracle.coherence.examples.sockshop.spring.catalog.model.Sock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Implementation of the Catalog Service {@code /catalogue} API.
 */
@RequestMapping("/catalogue")
@RestController
public class CatalogController {
    private static final Logger LOGGER = Logger.getLogger(CatalogController.class.getName());

    private final CatalogRepository catalog;

    public CatalogController(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    @Operation(summary = "Return the socks that match the specified query parameters")
    public Collection<? extends Sock> getSocks(
            @Parameter(description = "tag identifiers")
            @RequestParam(value = "tags", required = false) String tags,
            @Parameter(name = "order", description = "order identifier")
            @RequestParam(value = "order", required = false, defaultValue = "price") String order,
            @Parameter(description = "page number")
            @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum,
            @Parameter(description = "page size")
            @RequestParam(value = "size", required = false, defaultValue = "10")  int pageSize) {
        LOGGER.info("CatalogResource.getSocks: size=" + pageSize);
        return catalog.getSocks(tags, order, pageNum, pageSize);
    }

    @GetMapping("size")
    @Operation(summary = "Return sock count for the specified tag identifiers")
    public Count getSockCount(
            @Parameter(description = "tag identifiers")
            @RequestParam(value = "tags", required = false) String tags) {
        return new Count(catalog.getSockCount(tags));
    }

    @GetMapping("{id}")
    @Operation(summary = "Return socks for the specified sock identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "if socks are found"),
            @ApiResponse(responseCode = "404", description = "if socks do not exist")
    })
    public ResponseEntity<Sock> getSock(
            @Parameter(description = "sock identifier")
            @PathVariable("id") String sockId) {
        Sock sock = catalog.getSock(sockId);
        return sock == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(sock);
    }

    @GetMapping(path = "images/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Return the sock images for the specified image identifer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "if image is found"),
            @ApiResponse(responseCode = "404", description = "if image does not exist")
    })
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "image identifier")
            @PathVariable("image") String image) {
        Resource imgResource = new ClassPathResource("web/images/" + image);
        return imgResource.exists()
				? ResponseEntity.ok(imgResource)
                : ResponseEntity.notFound().build();
    }

    public static class Count {
        public long size;
        public Object err;

        public Count(long size) {
            this.size = size;
        }
    }

}
