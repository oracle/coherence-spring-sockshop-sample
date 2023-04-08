/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.service;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Address;
import com.oracle.coherence.examples.sockshop.spring.payment.model.Authorization;
import com.oracle.coherence.examples.sockshop.spring.payment.model.Card;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Trivial {@link PaymentService} implementation for demo and testing purposes.
 * <p/>
 * It approves all payment requests with the total amount lower or equal
 * to the {@code payment.limit} configuration property (100 by default),
 * and declines all requests above that amount.
 */
@Component
public class DefaultPaymentService implements PaymentService {
    @Autowired
    private MeterRegistry registry;

    /**
     * Payment limit
     */
    private float paymentLimit;

    /**
     * Construct {@code DefaultPaymentService} instance.
     */
    public DefaultPaymentService() {
    }

     /**
     * Construct {@code DefaultPaymentService} instance with {@link MeterRegistry}s for testing purposes.
     */
    public DefaultPaymentService(float paymentLimit, MeterRegistry registry) {
        this.paymentLimit = paymentLimit;
        this.registry = registry;
    }

    /**
     * Construct {@code DefaultPaymentService} instance.
     *
     * @param paymentLimit payment limit
     */
    @Autowired
    public DefaultPaymentService(@Value("${payment.limit:100}") float paymentLimit) {
        this.paymentLimit = paymentLimit;
    }

    @Override
    public Authorization authorize(String orderId, String firstName, String lastName, Card card, Address address, float amount) {
        boolean fAuthorized = amount > 0 && amount <= paymentLimit;

        String message = fAuthorized ? "Payment authorized." :
                amount <= 0 ? "Invalid payment amount." :
                        "Payment declined: amount exceeds " + String.format("%.2f", paymentLimit);

        if (fAuthorized) {
            registry.counter("payment.success").increment();
        } else {
            registry.counter("payment.failure").increment();
        }

        return Authorization.builder()
                .orderId(orderId)
                .time(LocalDateTime.now())
                .authorised(fAuthorized)
                .message(message)
                .build();
    }
}
