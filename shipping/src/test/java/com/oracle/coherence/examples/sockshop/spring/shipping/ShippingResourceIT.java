/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.shipping;

import java.time.LocalDate;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static com.oracle.coherence.examples.sockshop.spring.shipping.TestDataFactory.shipment;
import static com.oracle.coherence.examples.sockshop.spring.shipping.TestDataFactory.shippingRequest;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link ShippingResource}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShippingResourceIT {

    @LocalServerPort
    int port;

    @Autowired
    private TestShipmentRepository shipments;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }


    @BeforeEach
    void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        shipments.clear();
    }

    @Test
    void testFedEx() {
        given().
                body(shippingRequest("A123", 1)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(HttpStatus.OK.value()).
                body("carrier", is("FEDEX"),
                     "deliveryDate", is(LocalDate.now().plusDays(1).toString())
                );
    }

    @Test
    void testUPS() {
        given().
                body(shippingRequest("A456", 3)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(HttpStatus.OK.value()).
                body("carrier", is("UPS"),
                     "deliveryDate", is(LocalDate.now().plusDays(3).toString())
                );
    }

    @Test
    void testUSPS() {
        given().
                body(shippingRequest("A789", 10)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/shipping").
        then().
                statusCode(HttpStatus.OK.value()).
                body("carrier", is("USPS"),
                     "deliveryDate", is(LocalDate.now().plusDays(5).toString())
                );
    }

    @Test
    void testGetShipmentByOrder() {
        LocalDate deliveryDate = LocalDate.now().plusDays(2);
        shipments.saveShipment(shipment("A123", "UPS", "1Z999AA10123456784", deliveryDate));

        when().
                get("/shipping/{orderId}", "A123").
        then().
                statusCode(HttpStatus.OK.value()).
                body("carrier", is("UPS"),
                     "trackingNumber", is("1Z999AA10123456784"),
                     "deliveryDate", is(deliveryDate.toString())
                );

        when().
                get("/shipments/{orderId}", "B456").
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }
}
