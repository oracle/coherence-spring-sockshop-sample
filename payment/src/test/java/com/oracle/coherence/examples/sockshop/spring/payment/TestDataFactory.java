/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment;

import com.oracle.coherence.examples.sockshop.spring.payment.controller.PaymentRequest;
import com.oracle.coherence.examples.sockshop.spring.payment.model.*;

import java.time.LocalDateTime;

/**
 * Helper methods to create test data.
 */
public class TestDataFactory {

    public static PaymentRequest paymentRequest(String orderId, float amount) {
        return PaymentRequest.builder()
                .orderId(orderId)
                .customer(customer())
                .address(address())
                .card(card())
                .amount(amount)
                .build();
    }

    public static Customer customer() {
        return Customer.builder()
                .firstName("Homer")
                .lastName("Simpson")
                .build();
    }

    public static Address address() {
        return Address.builder()
                .number("123")
                .street("Main St")
                .city("Springfield")
                .postcode("55555")
                .country("USA")
                .build();
    }

    public static Card card() {
        return Card.builder()
                .longNum("************1234")
                .expires("10/2025")
                .ccv("789")
                .build();
    }

    public static Authorization auth(String orderId, LocalDateTime time, boolean fAuthorized, String message) {
        return Authorization.builder()
                .orderId(orderId)
                .time(time)
                .authorised(fAuthorized)
                .message(message)
                .build();
    }

    public static Authorization auth(String orderId, LocalDateTime time, Err error) {
        return Authorization.builder()
                .orderId(orderId)
                .time(time)
                .authorised(false)
                .error(error)
                .build();
    }
}
