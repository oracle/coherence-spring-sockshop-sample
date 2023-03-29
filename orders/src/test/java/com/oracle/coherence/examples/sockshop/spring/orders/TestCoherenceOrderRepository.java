/*
 * Copyright (c) 2020, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.examples.sockshop.spring.orders;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Order;
import com.oracle.coherence.examples.sockshop.spring.orders.repository.CoherenceOrderRepository;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

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
