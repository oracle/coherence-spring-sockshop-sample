/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

/**
 * Business interface for the {@code OrderProcessor} service.
 */
public interface OrderProcessor {
    /**
     * Process new order.
     *
     * @param order  the order to process
     */
    void processOrder(Order order);
}
