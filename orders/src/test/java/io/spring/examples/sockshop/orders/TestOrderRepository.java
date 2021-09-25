/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.spring.examples.sockshop.orders;

import io.spring.examples.sockshop.orders.repository.OrderRepository;

/**
 * Tests may need additional methods in repository implementations
 * in order to pre-load test data, clear the repository between the
 * test, etc.
 *
 * <p>The methods needed only for the tests should reside in the test
 * project, to avoid polluting production repository implementation
 * with methods it a) doesn't need, and b) may be downright dangerous.
 *
 * <p>Note that for the CDI to work it is necessary to mark the test
 * repositories with @Alternative and @Priority exceeding that of the
 * implementation.
 *
 * <p>Note that for the tests to test the actual implementation it is
 * necessary that the test implementations only extend the repository
 * class and add methods, but do not override any methods from the
 * repository implementation they are testing.
 *
 * @author Oleksandr Otenko  2020.03.06
 */
public interface TestOrderRepository extends OrderRepository {
    /**
     * Helper to clear this repository for testing.
     */
    void clear();

    /**
     * Return the identifier of the last saved order
     *
     * @return the identifier of the last saved order
     */
    default String getLastOrderId() {
        return null;
    }
}
