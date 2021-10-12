/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Shipment;
import com.oracle.coherence.examples.sockshop.spring.orders.model.ShippingRequest;
import com.oracle.coherence.examples.sockshop.spring.orders.service.ShippingClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.oracle.coherence.examples.sockshop.spring.orders.TestDataFactory.shipment;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Component
@Primary
public class TestShippingClient implements ShippingClient {
   public TestShippingClient() {
   }

   public Shipment ship(ShippingRequest request) {
      return shipment(request.getCustomer().getId());
   }
}
