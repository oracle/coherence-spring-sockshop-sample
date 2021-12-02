/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.shipping.controller;

import com.oracle.coherence.examples.sockshop.spring.shipping.model.Shipment;
import com.oracle.coherence.examples.sockshop.spring.shipping.repository.ShipmentRepository;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Implementation of the Shipping Service REST API.
 */
@RequestMapping("/shipping")
@RestController
public class ShippingController {
    /**
     * Shipment repository to use.
     */
    private final ShipmentRepository shipments;

    public ShippingController(ShipmentRepository shipments) {
        this.shipments = shipments;
    }

    @GetMapping(value = "{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Return the Shipment for the specified order")
    public Shipment getShipmentByOrderId(
            @Parameter(description = "Order identifier")
            @PathVariable("orderId") String orderId) {
        return shipments.getShipment(orderId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ship the specified shipping request")
    @Timed("ship")
    public Shipment ship(
            @Parameter(description = "Shipping request")
            @RequestBody ShippingRequest req) {
        // defaults
        String carrier = "USPS";
        String trackingNumber = "9205 5000 0000 0000 0000 00";
        LocalDate deliveryDate = LocalDate.now().plusDays(5);

        if (req.getItemCount() == 1) {  // use FedEx
            carrier = "FEDEX";
            trackingNumber = "231300687629630";
            deliveryDate = LocalDate.now().plusDays(1);
        }
        else if (req.getItemCount() <= 3) {  // use UPS
            carrier = "UPS";
            trackingNumber = "1Z999AA10123456784";
            deliveryDate = LocalDate.now().plusDays(3);
        }

        Shipment shipment = Shipment.builder()
                .orderId(req.getOrderId())
                .carrier(carrier)
                .trackingNumber(trackingNumber)
                .deliveryDate(deliveryDate)
                .build();

        shipments.saveShipment(shipment);

        return shipment;
    }
}
