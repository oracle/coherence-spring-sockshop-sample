/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.model.Address;
import com.oracle.coherence.examples.sockshop.spring.users.model.AddressId;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;

import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for {@link AddressController}.
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"coherence.localhost=127.0.0.1",
				"coherence.ttl=0",
				"java.net.preferIPv4Stack=true",
				"coherence.wka=127.0.0.1"
		}
)
public class AddressControllerTests {

	@Autowired
	private ServletWebServerApplicationContext webServerApplicationConext;

	@Autowired
	ApplicationContext context;

	private UserService userService;

	@BeforeEach
	void setup() {
		// Configure RestAssured to run tests against our application
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = this.webServerApplicationConext.getWebServer().getPort();
        userService = getUserService();
        userService.removeUser("foouser");
        userService.removeUser("baruser");
	}

	protected UserService getUserService() {
		return context.getBean(UserService.class);
	}

	@Test
	public void testRegisterAddress() {
        userService.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
		given().
			contentType(JSON).
			body(new AddressController.AddAddressRequest("16", "huntington", "lexington", "01886", "us", "foouser")).
		when().
			post("/addresses").
		then().
			log().body()
				.statusCode(200)
				.body("id", equalTo("foouser:1"));
	}

	@Test
	public void testGetAddress() {
		final User user = new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass");
		final Address address = new Address("555", "woodbury St", "Westford", "01886", "USA");
		final AddressId addressId = user.addAddress(address).getAddressId();
        userService.register(user);

		given().
			  pathParam("id", addressId.toString()).
		when().
			  get("/addresses/{id}").
		then()
				.log()
				.body()
				.statusCode(HttpStatus.OK.value())
				.body("street", is("woodbury St"))
				.body("number", is("555"))
				.body("country", is("USA"))
				.body("city", is("Westford"))
				.body("postcode", is("01886"))
				.body("id", is("foouser:1"))
				.body("$", hasKey("_links"));
	}

	@Test
	public void testDeleteAddress() {
		User user = userService.getUser("foouser");

		if (user == null) {
			user = new User("foouser", "passfoo", "foo@weavesocks.com", "foouser", "not-a-secret");
		}

		final Address address = new Address("555", "woodbury St", "Westford", "01886", "USA");
		final AddressId addressId = user.addAddress(address).getAddressId();
		userService.register(user);

		given().
			pathParam("id", addressId.toString()).
		when().
			delete("/addresses/{id}").
		then()
			.log()
			.body()
			.statusCode(200)
				.body("status", is(true));
	}
}
