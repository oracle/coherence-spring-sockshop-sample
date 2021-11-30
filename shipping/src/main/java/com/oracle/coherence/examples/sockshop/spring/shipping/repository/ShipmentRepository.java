/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.shipping.repository;

import com.oracle.coherence.examples.sockshop.spring.shipping.model.Shipment;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface ShipmentRepository {
    /**
     * Return shipment for the specified order.
     *
     * @param orderId the order identifier
     *
     * @return the shipment for the specified order;
     *         {@code null} if the shipment doesn't exist
     */
    Shipment getShipment(String orderId);

    /**
     * Save shipment details into the repository.
     *
     * @param shipment the shipment to save
     */
    void saveShipment(Shipment shipment);
}
