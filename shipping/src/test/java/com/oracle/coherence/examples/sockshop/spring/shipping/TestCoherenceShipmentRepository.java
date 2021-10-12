/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.shipping;

import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Primary
@Component
public class TestCoherenceShipmentRepository extends CoherenceShipmentRepository implements TestShipmentRepository {
    @Inject
    TestCoherenceShipmentRepository(@CoherenceMap("shipments") NamedMap<String, Shipment> shipments) {
        super(shipments);
    }

    @Override
    public void clear() {
        shipments.clear();
    }
}
