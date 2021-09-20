/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.payment;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.spring.examples.sockshop.payment.TestDataFactory.address;
import static io.spring.examples.sockshop.payment.TestDataFactory.card;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for {@link DefaultPaymentService}.
 */
public class DefaultPaymentServiceTest {

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();
    private PaymentService service;

    @BeforeEach
    void initCounters() {
        service = new DefaultPaymentService(100, meterRegistry);
    }

    @Test
    void testSuccessfulAuthorization() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), 50);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(true));
        assertThat(auth.getMessage(), is("Payment authorized."));
        assertThat(auth.getError(), nullValue());
    }

    @Test
    void testDeclinedAuthorization() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), 150);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(false));
        assertThat(auth.getMessage(), is("Payment declined: amount exceeds 100.00"));
        assertThat(auth.getError(), nullValue());
    }

    @Test
    void testInvalidAmount() {
        Authorization auth = service.authorize("A123", "Homer", "Simpson", card(), address(), -25);

        assertThat(auth.getOrderId(), is("A123"));
        assertThat(auth.getTime(), lessThanOrEqualTo(LocalDateTime.now()));
        assertThat(auth.isAuthorised(), is(false));
        assertThat(auth.getMessage(), is("Invalid payment amount."));
        assertThat(auth.getError(), nullValue());
    }
}
