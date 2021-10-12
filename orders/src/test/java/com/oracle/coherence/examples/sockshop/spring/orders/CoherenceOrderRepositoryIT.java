/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.oracle.coherence.examples.sockshop.spring.orders.TestDataFactory.order;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests for Coherence repository implementation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CoherenceOrderRepositoryIT {
    @Autowired
    private TestOrderRepository orders;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }

    @BeforeEach
    void setup() {
        orders.clear();
    }

    @Test
    void testFindOrdersByCustomer() {
        orders.saveOrder(order("homer", 1));
        orders.saveOrder(order("homer", 2));
        orders.saveOrder(order("marge", 5));

        assertThat(orders.findOrdersByCustomer("homer").size(), is(2));
        assertThat(orders.findOrdersByCustomer("marge").size(), is(1));
    }

    @Test
    void testOrderCreation() {
        Order order = order("homer", 1);
        orders.saveOrder(order);

        assertThat(orders.get(order.getOrderId()), is(order));
    }
}