/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders.service;

import io.spring.examples.sockshop.orders.model.Address;
import io.spring.examples.sockshop.orders.model.Card;
import io.spring.examples.sockshop.orders.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user", url = "http://user/", primary = false)
public interface UsersClient {
   @GetMapping(value = "/addresses/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE)
   Address address(@PathVariable("addressId") String addressId);

   @GetMapping(value = "/cards/{cardId}", consumes = MediaType.APPLICATION_JSON_VALUE)
   Card card(@PathVariable("cardId") String cardId);

   @GetMapping(value = "/customers/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
   Customer customer(@PathVariable("customerId") String customerId);
}
