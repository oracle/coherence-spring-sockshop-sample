/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders.repository;

import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import com.tangosol.util.Filters;
import io.spring.examples.sockshop.orders.model.Order;
import io.spring.examples.sockshop.orders.repository.OrderRepository;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

/**
 * An implementation of {@link OrderRepository}
 * that that uses Coherence as a backend data store.
 */
@Component
public class CoherenceOrderRepository implements OrderRepository {
    protected NamedMap<String, Order> orders;

    @Inject
    public CoherenceOrderRepository(@CoherenceMap("orders") NamedMap<String, Order> orders) {
        this.orders = orders;
    }

    @Override
    public Collection<? extends Order> findOrdersByCustomer(String customerId) {
        Collection<Order> customerOrders = orders.values(Filters.equal(o -> ((Order) o).getCustomer().getId(), customerId), null);
        return customerOrders.isEmpty() ? Collections.emptyList() : customerOrders;
    }

    @Override
    public Order get(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public void saveOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }
}
