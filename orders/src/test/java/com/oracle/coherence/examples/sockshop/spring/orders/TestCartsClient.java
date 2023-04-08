/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Item;
import com.oracle.coherence.examples.sockshop.spring.orders.service.CartsClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.oracle.coherence.examples.sockshop.spring.orders.TestDataFactory.items;

@Component
@Primary
public class TestCartsClient implements CartsClient {
   public TestCartsClient() {
   }

   public List<Item> cart(String cartId) {
      return items(3);
   }
}
