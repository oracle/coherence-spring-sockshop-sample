/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.controller;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for {@link PaymentController},
 * using Coherence for persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoherencePaymentResourceIT extends PaymentResourceIT {
}
