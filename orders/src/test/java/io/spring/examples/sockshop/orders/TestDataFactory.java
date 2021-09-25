/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.spring.examples.sockshop.orders;

import io.spring.examples.sockshop.orders.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to create test data.
 */
public class TestDataFactory {
    public static Order order(String customerId, int itemCount) {
        List<Item> items = items(itemCount);

        Order order = Order.builder()
                .customer(customer(customerId))
                .address(address())
                .card(card())
                .items(items)
                .build();
        Payment payment = payment(customerId);
        order.setPayment(payment);
        order.setStatus(payment == null || !payment.isAuthorised() ? Order.Status.PAYMENT_FAILED : Order.Status.PAID);
        if (order.getStatus() == Order.Status.PAID) {
            order.setShipment(shipment(customerId));
            order.setStatus(Order.Status.SHIPPED);
        }
        return order;
    }

    public static Customer customer(String id) {
        return Customer.builder()
                .id(id)
                .firstName(Character.toUpperCase(id.charAt(0)) + id.substring(1))
                .lastName("Simpson")
                .email(id + "@simpson.org")
                .build();
    }

    public static Address address() {
        return Address.builder()
                .number("123")
                .street("Main St")
                .city("Springfield")
                .postcode("55555")
                .country("USA")
                .build();
    }

    public static Card card() {
        return Card.builder()
                .longNum("************1234")
                .expires("10/2025")
                .ccv("789")
                .build();
    }

    public static List<Item> items(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            items.add(item(Integer.toString(i), i, Integer.valueOf(i).floatValue()));
        }
        return items;
    }

    public static Item item(String id, int quantity, float price) {
        return Item.builder()
                .itemId(id)
                .quantity(quantity)
                .unitPrice(price)
                .build();
    }

    public static Payment payment(String customerId) {
        if ("bart".equals(customerId)) {
            return Payment.builder()
                    .authorised(false)
                    .message("Minors need parent approval")
                    .build();
        }
        if ("lisa".equals(customerId)) {
            return null;
        }
        return Payment.builder()
                .authorised(true)
                .message("Payment authorized")
                .build();
    }

    public static Shipment shipment(String customerId) {
        if ("marge".equals(customerId)) {
            return Shipment.builder()
                    .carrier("FedEx")
                    .trackingNumber("231300687629630")
                    .deliveryDate(LocalDate.now().plusDays(1))
                    .build();
        }
        return Shipment.builder()
                .carrier("UPS")
                .trackingNumber("1Z999AA10123456784")
                .deliveryDate(LocalDate.now().plusDays(2))
                .build();
    }
}
