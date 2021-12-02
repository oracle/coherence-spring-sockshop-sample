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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Implementation of the Payment Service REST API.
 */
@RequestMapping("/payments")
@RestController
public class PaymentController {
    /**
     * Payment repository to use.
     */
    private final PaymentRepository payments;

    /**
     * Payment service to use.
     */
    private final PaymentService paymentService;

    private final MeterRegistry meterRegistry;

    public PaymentController(PaymentRepository payments, PaymentService paymentService, MeterRegistry meterRegistry) {
        this.payments = payments;
        this.paymentService = paymentService;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping(value = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return the payment authorization for the specified order")
    public ResponseEntity<Collection<? extends Authorization>> getOrderAuthorizations(
            @Parameter(description = "Order identifier")
            @PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(payments.findAuthorizationsByOrder(orderId));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authorize a payment request")
    public Authorization authorize(
            @Parameter(description = "Payment request")
            @RequestBody PaymentRequest paymentRequest) {
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
