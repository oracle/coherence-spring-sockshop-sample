/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts.controller;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import com.oracle.coherence.examples.sockshop.spring.carts.repository.CartRepository;
import io.restassured.RestAssured;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link CartRepository}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartResourceIT {

    protected CartRepository carts;

    @LocalServerPort
    int port;

    /**
     * This will start the application on ephemeral port to avoid port conflicts.
     */
    @BeforeAll
    static void startServer() {
        // disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }

    /**
     * Stop the server, as we cannot have multiple servers started at the same time.
     */
    @AfterAll
    static void stopServer() {
    }

    @Autowired
    void setCartRepository(CartRepository cartRepository) {
        this.carts = cartRepository;
    }

    @BeforeEach
    void setup() {
        // Configure RestAssured to run tests against our application
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = getBasePath();

        carts.deleteCart("C1");
        carts.deleteCart("C2");
    }

    protected String getBasePath() {
        return "/carts";
    }

    @Test
    public void testGetCart() {
        when().
                get("/{cartId}", "C1").
        then().
                statusCode(200).
                body("customerId", equalTo("C1"),
                     "items", nullValue());
    }

    @Test
    void testDeleteCart() {
        carts.addItem("C1", new Item("X1", 5, 10f));
        when().
                delete("/{cartId}", "C000").
        then().
                statusCode(HttpStatus.NOT_FOUND.value());

        when().
                delete("/{cartId}", "C1").
        then().
                statusCode(HttpStatus.ACCEPTED.value());
    }

    @Test
    void testMergeNonExistentCarts() {
        given().
                queryParam("sessionId", "FOO").
        when().
                get("/{cartId}/merge", "C1").
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testMergeCarts() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C2", new Item("X1", 5, 10f));
        carts.addItem("C2", new Item("X2", 5, 10f));

        // it should succeed the first time
        given().
                queryParam("sessionId", "C2").
        when().
                get("/{cartId}/merge", "C1").
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        assertThat(carts.getItems("C1").size(), is(2));
        assertThat(carts.getItem("C1", "X1").getQuantity(), is(10));

        // now that it has been merged, it should fail
        given().
                queryParam("sessionId", "C2").
        when().
                get("/{cartId}/merge", "C1").
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testGetItems() {
        // should be empty to start
        when().
                get("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.OK.value()).
                body("$", empty());

        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 5, 10f));

        // now it should return two items
        when().
                get("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.OK.value()).
                body("itemId", hasItems("X1", "X2"));
    }

    @Test
    void testAddItem() {
        given().
                contentType(JSON).
                body(new Item("X1", 0, 10f)).
        when().
                post("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("itemId", is("X1"),
                     "quantity", is(1),
                     "unitPrice", is(10f));

        // if we do it again the quantity should increase
        given().
                contentType(JSON).
                body(new Item("X1", 3, 10f)).
        when().
                post("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.CREATED.value()).
                body("itemId", is("X1"),
                     "quantity", is(4),
                     "unitPrice", is(10f));
    }

    @Test
    void testUpdateItem() {
        given().
                contentType(JSON).
                body(new Item("X1", 5, 10f)).
        when().
                patch("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        assertThat(carts.getItem("C1", "X1").getQuantity(), is(5));

        // if we do it again the quantity should be overwritten
        given().
                contentType(JSON).
                body(new Item("X1", 3, 10f)).
        when().
                patch("/{cartId}/items", "C1").
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        assertThat(carts.getItem("C1", "X1").getQuantity(), is(3));
    }

    @Test
    void testGetItem() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 3, 17f));

        when().
                get("/{cartId}/items/{itemId}", "C1", "X1").
        then().
                statusCode(HttpStatus.OK.value()).
                body("itemId", is("X1"),
                     "quantity", is(5),
                     "unitPrice", is(10f));
    }

    @Test
    void testDeleteItem() {
        // let's add some data to the repo
        carts.addItem("C1", new Item("X1", 5, 10f));
        carts.addItem("C1", new Item("X2", 3, 17f));

        when().
                delete("/{cartId}/items/{itemId}", "C1", "X1").
        then().
                statusCode(HttpStatus.ACCEPTED.value());

        assertThat(carts.getItems("C1").size(), is(1));
        assertThat(carts.getItem("C1", "X1"), nullValue());
    }
}
