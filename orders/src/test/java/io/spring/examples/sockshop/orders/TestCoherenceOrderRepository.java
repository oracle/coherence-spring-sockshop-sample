/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.spring.examples.sockshop.orders;

import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import javax.inject.Inject;

@Component
@Primary
public class TestCoherenceOrderRepository extends CoherenceOrderRepository
        implements TestOrderRepository {
    private String lastOrderId;

    @Inject
    public TestCoherenceOrderRepository(@CoherenceMap("orders") NamedMap<String, Order> orders) {
        super(orders);
    }

    public void clear() {
        orders.clear();
    }

    @Override
    public void saveOrder(Order order) {
        super.saveOrder(order);
        lastOrderId = order.getOrderId();
    }

    @Override
    public String getLastOrderId() {
        return lastOrderId;
    }
}
