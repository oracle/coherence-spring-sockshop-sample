/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client-side interface for Carts REST service.
 */
@FeignClient(value = "carts", url = "http://carts/", primary = false)
public interface CartsClient {
   /**
    * Get cart items.
    *
    * @param cartId  cart identifier
    *
    * @return cart items from the specified cart
    */
   @GetMapping(value = "/carts/{cartId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
   List<Item> cart(@PathVariable("cartId") String cartId);
}
