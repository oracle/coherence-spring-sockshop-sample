/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders.service.support;

/**
 * Base class for all business-level order processing exceptions.
 */
public class OrderException extends IllegalStateException {
    /**
     * Construct {@code OrderException} instance.
     *
     * @param message error message
     */
    public OrderException(String message) {
        super(message);
    }
}
