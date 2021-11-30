/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.shipping.repository;

import com.oracle.coherence.examples.sockshop.spring.shipping.model.Shipment;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

/**
 * An implementation of {@link ShipmentRepository}
 * that that uses Coherence as a backend data store.
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Component
public class CoherenceShipmentRepository implements ShipmentRepository {
    protected Map<String, Shipment> shipments;

    @Inject
    public CoherenceShipmentRepository(@CoherenceMap("shipments") NamedMap<String, Shipment> shipments) {
        this.shipments = shipments;
    }

    @Override
    public Shipment getShipment(String orderId) {
        return shipments.get(orderId);
    }

    @Override
    public void saveShipment(Shipment shipment) {
        shipments.put(shipment.getOrderId(), shipment);
    }
}
