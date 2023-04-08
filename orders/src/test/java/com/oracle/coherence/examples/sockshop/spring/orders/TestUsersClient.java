/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Address;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Card;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Customer;
import com.oracle.coherence.examples.sockshop.spring.orders.service.UsersClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TestUsersClient implements UsersClient {
   public TestUsersClient() {
   }

   public Address address(String addressId) {
      return TestDataFactory.address();
   }

   public Card card(String cardId) {
      return TestDataFactory.card();
   }

   public Customer customer(String customerId) {
      return TestDataFactory.customer(customerId);
   }
}
