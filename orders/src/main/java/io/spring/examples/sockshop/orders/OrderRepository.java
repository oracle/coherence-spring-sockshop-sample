/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import java.util.Collection;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface OrderRepository {
    /**
     * Find all orders for the specified customer.
     *
     * @param customerId the customer to find the orders for
     *
     * @return all orders for the specified customer
     */
    Collection<? extends Order> findOrdersByCustomer(String customerId);

    /**
     * Get an existing order.
     *
     * @param orderId the order identifier to get the order for
     *
     * @return an existing order, or {@code null} if the specified order
     *         does not exist
     */
    Order get(String orderId);

    /**
     * Save order.
     *
     * @param order the order to save
     */
    void saveOrder(Order order);
}
