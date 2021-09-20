/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.orders;

import com.oracle.bedrock.testsupport.deferred.Eventually;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.time.LocalDate;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link OrderResource},
 * using Coherence for persistence.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoherenceOrderResourceIT {

	@LocalServerPort
    int port;

    @Autowired
    protected TestOrderRepository orders;


    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }


    @BeforeEach
    protected void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        orders.clear();
    }

    @Test
    protected void testGetMissingOrder() {
        when().
                get("/orders/{orderId}", "XYZ").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    protected void testGetOrder() {
        Order order = TestDataFactory.order("homer", 1);
        orders.saveOrder(order);
        Order saved = get("/orders/{orderId}", order.getOrderId()).as(Order.class, ObjectMapperType.JACKSON_2);

        assertThat(saved, is(order));
    }

    @Test
    protected void testFindOrdersByCustomerId() {
        orders.saveOrder(TestDataFactory.order("homer", 1));
        orders.saveOrder(TestDataFactory.order("homer", 2));
        orders.saveOrder(TestDataFactory.order("marge", 5));

        given().
                queryParam("custId", "homer").
                when().
                get("/orders/search/customerId").
                then().
                statusCode(HttpStatus.OK.value()).
                body("_embedded.customerOrders.total", containsInAnyOrder(1f, 5f));

        given().
                queryParam("custId", "marge").
                when().
                get("/orders/search/customerId").
                then().
                statusCode(HttpStatus.OK.value()).
                body("_embedded.customerOrders.total", containsInAnyOrder(55f));

        given().
                queryParam("custId", "bart").
                when().
                get("/orders/search/customerId").

                then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }


    @Test
    protected void testInvalidOrder() {
        NewOrderRequest req = NewOrderRequest.builder().build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                post("/orders").
                then().
                log().everything().
                statusCode(HttpStatus.NOT_ACCEPTABLE.value())
                .body("message", is("Invalid order request. Order requires customer, address, card and items."));
    }

    @Test
    protected void testCreateOrder() {
        String baseUri = "http://localhost:" + port;
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/homer"))
                .address(URI.create(baseUri + "/addresses/homer:1"))
                .card(URI.create(baseUri + "/cards/homer:1234"))
                .items(URI.create(baseUri + "/carts/homer/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("total", is(14.0f),
                     "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), Matchers.is(Order.Status.SHIPPED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(true));
        assertThat(order.getShipment().getCarrier(), is("UPS"));
        assertThat(order.getShipment().getDeliveryDate(), is(LocalDate.now().plusDays(2)));
    }

    @Test
    protected void testPaymentFailure() {
        String baseUri = "http://localhost:" + port;
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/lisa"))
                .address(URI.create(baseUri + "/addresses/lisa:1"))
                .card(URI.create(baseUri + "/cards/lisa:1234"))
                .items(URI.create(baseUri + "/carts/lisa/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("total", is(14.0f),
                    "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), Matchers.is(Order.Status.PAYMENT_FAILED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(false));
        assertThat(order.getPayment().getMessage(), is("Unable to parse authorization packet"));
    }

    @Test
    protected void testPaymentDeclined() {
        String baseUri = "http://localhost:" + port;
        NewOrderRequest req = NewOrderRequest.builder()
                .customer(URI.create(baseUri + "/customers/bart"))
                .address(URI.create(baseUri + "/addresses/bart:1"))
                .card(URI.create(baseUri + "/cards/bart:1234"))
                .items(URI.create(baseUri + "/carts/bart/items"))
                .build();

        given().
                body(req).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
        when().
                post("/orders").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("total", is(14.0f),
                    "status", is("CREATED"));

        final String orderId = orders.getLastOrderId();
        Eventually.assertDeferred(() -> orders.get(orderId).getStatus(), Matchers.is(Order.Status.PAYMENT_FAILED));

        Order order = orders.get(orderId);
        assertThat(order.getPayment().isAuthorised(), is(false));
        assertThat(order.getPayment().getMessage(), is("Minors need parent approval"));
    }
}