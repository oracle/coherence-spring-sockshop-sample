/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.payment;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for {@link PaymentResource},
 * using Coherence for persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoherencePaymentResourceIT extends PaymentResourceIT {
}
