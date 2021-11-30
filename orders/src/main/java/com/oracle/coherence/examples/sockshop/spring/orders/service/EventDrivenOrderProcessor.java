/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.service;

import com.oracle.coherence.examples.sockshop.spring.orders.repository.OrderRepository;
import com.oracle.coherence.examples.sockshop.spring.orders.service.support.PaymentDeclinedException;
import com.oracle.coherence.spring.annotation.event.Inserted;
import com.oracle.coherence.spring.annotation.event.MapName;
import com.oracle.coherence.spring.annotation.event.Updated;
import com.oracle.coherence.spring.event.CoherenceEventListener;
import com.tangosol.net.events.partition.cache.EntryEvent;
import com.oracle.coherence.examples.sockshop.spring.orders.model.PaymentRequest;
import com.oracle.coherence.examples.sockshop.spring.orders.model.ShippingRequest;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Order;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Payment;
import com.oracle.coherence.examples.sockshop.spring.orders.model.Shipment;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * A more realistic implementation of {@link OrderProcessor} that stores
 * submitted order immediately and uses Coherence server-side events
 * to process payment and ship the order asynchronously, based on the
 * order status.
 */
@Slf4j
@Component
public class EventDrivenOrderProcessor implements OrderProcessor {
    /**
     * Order repository to use.
     */
    @Autowired
    protected OrderRepository orders;

    /**
     * Shipping service client.
     */
    @Autowired
    protected ShippingClient shippingService;

    /**
     * Payment service client.
     */
    @Autowired
    protected PaymentClient paymentService;

    // --- OrderProcessor interface -----------------------------------------

    @Override
    public void processOrder(Order order) {
        saveOrder(order);
    }
    // ---- helpers ---------------------------------------------------------

    /**
     * Save specified order.
     *
     * @param order the order to save
     */
    protected void saveOrder(Order order) {
        orders.saveOrder(order);
        log.info("Order saved: " + order);
    }

    /**
     * Process payment and update order with payment details.
     *
     * @param order the order to process the payment for
     *
     * @throws PaymentDeclinedException if the payment was declined
     */
    protected void processPayment(Order order) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getOrderId())
                .customer(order.getCustomer())
                .address(order.getAddress())
                .card(order.getCard())
                .amount(order.getTotal())
                .build();

        log.info("Processing Payment: " + paymentRequest);
        Payment payment = paymentService.authorize(paymentRequest);
        if (payment == null) {
            payment = Payment.builder()
                    .authorised(false)
                    .message("Unable to parse authorization packet")
                    .build();
        }
        log.info("Payment processed: " + payment);

        order.setPayment(payment);
        if (!payment.isAuthorised()) {
            order.setStatus(Order.Status.PAYMENT_FAILED);
            throw new PaymentDeclinedException(payment.getMessage());
        }

        order.setStatus(Order.Status.PAID);
    }

    /**
     * Submits order for shipping and updates order with shipment details.
     *
     * @param order the order to ship
     */
    protected void shipOrder(Order order) {
        ShippingRequest shippingRequest = ShippingRequest.builder()
                .orderId(order.getOrderId())
                .customer(order.getCustomer())
                .address(order.getAddress())
                .itemCount(order.getItems().size())
                .build();

        log.info("Creating Shipment: " + shippingRequest);
        Shipment shipment = shippingService.ship(shippingRequest);
        log.info("Created Shipment: " + shipment);

        order.setShipment(shipment);
        order.setStatus(Order.Status.SHIPPED);
    }

    // ---- helper methods --------------------------------------------------

    @Async
    @CoherenceEventListener
    void onOrderCreated(@Inserted @Updated @MapName("orders") EntryEvent<String, Order> event) {
        Order order = event.getValue();
        log.info("event type: " + event.getType());
        log.info("order status: " + order.getStatus());

        switch (order.getStatus()) {
        case CREATED:
            try {
                processPayment(order);
            }
            finally {
                saveOrder(order);
            }
            break;

        case PAID:
            try {
                shipOrder(order);
            }
            finally {
                saveOrder(order);
            }
            break;

        default:
            // do nothing, order is in a terminal state already
        }
    }
}
