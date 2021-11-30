/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.controller;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Authorization;
import com.oracle.coherence.examples.sockshop.spring.payment.repository.PaymentRepository;
import com.oracle.coherence.examples.sockshop.spring.payment.service.PaymentService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Implementation of the Payment Service REST API.
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@RequestMapping("/payments")
@RestController
public class PaymentResource implements PaymentApi {
    /**
     * Payment repository to use.
     */
    @Autowired
    private PaymentRepository payments;

    /**
     * Payment service to use.
     */
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    public ResponseEntity<Collection<? extends Authorization>> getOrderAuthorizations(String orderId) {
        return ResponseEntity.ok(payments.findAuthorizationsByOrder(orderId));
    }

    @Override
    public Authorization authorize(PaymentRequest paymentRequest) {
        meterRegistry.counter("payments.authorize", "controller", "payments").increment();
        String firstName = paymentRequest.getCustomer().getFirstName();
        String lastName  = paymentRequest.getCustomer().getLastName();

        Authorization auth = paymentService.authorize(
                paymentRequest.getOrderId(),
                firstName,
                lastName,
                paymentRequest.getCard(),
                paymentRequest.getAddress(),
                paymentRequest.getAmount());

        payments.saveAuthorization(auth);

        return auth;
    }


}
