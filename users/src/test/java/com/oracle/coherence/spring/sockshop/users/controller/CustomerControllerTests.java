/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.model.Address;
import com.oracle.coherence.spring.sockshop.users.model.Card;
import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link AddressController}.
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTests {

	@Autowired
	private ServletWebServerApplicationContext webServerApplicationConext;

	@Autowired
	ApplicationContext context;

	private UserService userService;

	@BeforeEach
	void setup() {
		// Configure RestAssured to run tests against our application
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = webServerApplicationConext.getWebServer().getPort();
		userService = getUserService();
		User user = new User("Test", "User", "user@weavesocks.com", "user", "pass");
		user.addCard(new Card("1234123412341234", "12/19", "123"));
		user.addAddress(new Address("123", "Main St", "Springfield", "12123", "USA"));
		userService.register(user);
	}

	protected UserService getUserService() {
		return context.getBean(UserService.class);
	}

	@Test
	public void testAllCustomers() {
		when().
			get("/customers").
		then().log().all().
			statusCode(200).
				body("size()", is(2));
	}

    @Test
    void testGetCustomer() {
        when().
            get("/customers/{id}", "user").
        then().log().all().
            statusCode(200).
            body("firstName", is("Test"));
    }

	@Test
	void testDeleteCustomer() {
		given().
			pathParam("id", "user").
		when().
			delete("/customers/{id}").
		then().
			statusCode(200).
			body("status", is(true));
	}

	@Test
	void testGetCustomerCards() {
		when().
			get("/customers/{id}/cards", "user").
		then().log().all()
			.statusCode(200)
			.body("size()", is(2));
	}

	@Test
	void testGetCustomerAddresses() {
		when().
			get("/customers/{id}/addresses", "user").
		then().
			statusCode(200).
			body("_embedded.address.size()", is(1));
	}
}
