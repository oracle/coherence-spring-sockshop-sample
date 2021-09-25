/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.spring.examples.sockshop.orders;

import io.spring.examples.sockshop.orders.model.Payment;
import io.spring.examples.sockshop.orders.model.PaymentRequest;
import io.spring.examples.sockshop.orders.service.PaymentClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.spring.examples.sockshop.orders.TestDataFactory.payment;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Component
@Primary
public class TestPaymentClient implements PaymentClient {
   public TestPaymentClient() {
   }

   public Payment authorize(PaymentRequest request) {
      return payment(request.getCustomer().getId());
   }
}
