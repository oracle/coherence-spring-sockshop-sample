/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.controller;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Err;
import com.oracle.coherence.examples.sockshop.spring.payment.repository.TestPaymentRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static com.oracle.coherence.examples.sockshop.spring.payment.TestDataFactory.auth;
import static com.oracle.coherence.examples.sockshop.spring.payment.TestDataFactory.paymentRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link PaymentController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentResourceIT {

    @LocalServerPort
    int port;

    @Autowired
    TestPaymentRepository payments;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }

    @BeforeEach
    void setup() throws Exception {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        payments.clear();
    }

    @Test
    void testSuccessfulAuthorization() {
        given().
                body(paymentRequest("A123", 50)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(HttpStatus.OK.value()).
                body("authorised", is(true),
                     "message", is("Payment authorized.")
                );
    }

    @Test
    void testDeclinedAuthorization() {
        given().
                body(paymentRequest("A123", 150)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(HttpStatus.OK.value()).
                body("authorised", is(false),
                     "message", is("Payment declined: amount exceeds 100.00")
                );
    }

    @Test
    void testInvalidPaymentAmount() {
        given().
                body(paymentRequest("A123", -50)).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/payments").
        then().
                statusCode(HttpStatus.OK.value()).
                body("authorised", is(false),
                     "message", is("Invalid payment amount.")
                );
    }

    @Test
    void testFindPaymentsByOrder() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        payments.saveAuthorization(auth("A123", time, new Err("Payment service unavailable")));
        payments.saveAuthorization(auth("A123", time.plusSeconds(5), false, "Payment declined"));
        payments.saveAuthorization(auth("A123", time.plusSeconds(10), true, "Payment processed"));
        payments.saveAuthorization(auth("B456", time, true, "Payment processed"));

        when().
                get("/payments/{orderId}", "A123").
        then().log().everything().
                statusCode(HttpStatus.OK.value()).
                body("$", hasSize(3));

        when().
                get("/payments/{orderId}", "B456").
        then().
                statusCode(HttpStatus.OK.value()).
                body("$", hasSize(1));

        when().
                get("/payments/{orderId}", "C789").
        then().
                statusCode(HttpStatus.OK.value()).
                body("$", hasSize(0));
    }
}
