/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Payment;
import com.oracle.coherence.examples.sockshop.spring.orders.model.PaymentRequest;
import com.oracle.coherence.examples.sockshop.spring.orders.service.PaymentClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static com.oracle.coherence.examples.sockshop.spring.orders.TestDataFactory.payment;

@Component
@Primary
public class TestPaymentClient implements PaymentClient {
   public TestPaymentClient() {
   }

   public Payment authorize(PaymentRequest request) {
      return payment(request.getCustomer().getId());
   }
}
