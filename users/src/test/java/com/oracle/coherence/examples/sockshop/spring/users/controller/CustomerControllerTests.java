/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.Card;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
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
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link AddressController}.
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTests {

	@Autowired
	private ServletWebServerApplicationContext webServerApplicationContext;

	@Autowired
	ApplicationContext context;

	private UserService userService;

	@BeforeEach
	void setup() {
		// Configure RestAssured to run tests against our application
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = webServerApplicationContext.getWebServer().getPort();
		userService = getUserService();
		User user = new User("Test", "User", "user@weavesocks.com", "cartman", "pass");
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
			statusCode(200)
				.body("_embedded.customer.size()", is(4))
				.body("_embedded.customer", hasItems(
					allOf(
						hasEntry("username", "user"),
						hasEntry("firstName", "User"),
						hasEntry("lastName", "Name"),
						hasEntry("email", "user@sockshop"),
						hasEntry("id", "user"),
						hasKey("_links")
					),
					allOf(
						hasEntry("username", "cartman"),
						hasEntry("firstName", "Test"),
						hasEntry("lastName", "User"),
						hasEntry("email", "user@weavesocks.com"),
						hasEntry("id", "cartman"),
						hasKey("_links")
					),
					allOf(
						hasEntry("username", "user1"),
						hasEntry("firstName", "User1"),
						hasEntry("lastName", "Name1"),
						hasEntry("email", "user1@sockshop"),
						hasEntry("id", "user1"),
						hasKey("_links")
					),
					allOf(
						hasEntry("username", "Eve_Berger"),
						hasEntry("firstName", "Eve"),
						hasEntry("lastName", "Berger"),
						hasEntry("email", "eve_berger@sockshop"),
						hasEntry("id", "Eve_Berger"),
						hasKey("_links")
					)
				));
	}

	@Test
	void testGetCustomer() {
		when().
			get("/customers/{id}", "cartman").
		then().log().all().
			statusCode(200)
				.body("username", is("cartman"))
				.body("firstName", is("Test"))
				.body("lastName", is("User"))
				.body("email", is("user@weavesocks.com"))
				.body("$", hasKey("password"))
				.body("id", is("cartman"))
				.body("_links", hasKey("customer"))
				.body("_links", hasKey("self"))
				.body("_links", hasKey("addresses"))
				.body("_links", hasKey("cards"));
	}

	@Test
	void testDeleteCustomer() {
		given().
			pathParam("id", "cartman").
		when().
			delete("/customers/{id}").
		then().
			statusCode(200).
			body("status", is(true));
	}

	@Test
	void testGetCustomerCards() {
		when().
			get("/customers/{id}/cards", "cartman").
		then().log().all()
			.statusCode(200)
				.body("_embedded.card.size()", is(1))
				.body("_embedded.card[0].longNum", is("1234123412341234"))
				.body("_embedded.card[0].expires", is("12/19"))
				.body("_embedded.card[0].ccv", is("123"))
				.body("_embedded.card[0].id", is("cartman:1234"))
				.body("_embedded.card[0]", hasKey("_links"))
				.body("$", hasKey("_links"));
	}

	@Test
	void testGetCustomerAddresses() {
		when().
			get("/customers/{id}/addresses", "cartman").
		then().log().body().
			statusCode(200)
				.body("_embedded.address.size()", is(1))
				.body("_embedded.address[0].street", is("Main St"))
				.body("_embedded.address[0].number", is("123"))
				.body("_embedded.address[0].country", is("USA"))
				.body("_embedded.address[0].city", is("Springfield"))
				.body("_embedded.address[0].postcode", is("12123"))
				.body("_embedded.address[0].id", is("cartman:1"))
				.body("_embedded.address[0]", hasKey("_links"))
				.body("$", hasKey("_links"));
	}
}
