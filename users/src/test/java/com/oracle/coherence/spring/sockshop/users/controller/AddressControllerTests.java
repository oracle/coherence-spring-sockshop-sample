/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.model.Address;
import com.oracle.coherence.spring.sockshop.users.model.AddressId;
import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.service.UserService;

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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link com.oracle.coherence.spring.sockshop.users.controller.AddressController}.
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
			statusCode(200).
			body("id", containsString("foouser"));
	}

	@Test
	public void testGetAddress() {
		User u = new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass");
		AddressId addrId = u.addAddress(new Address("555", "woodbury St", "Westford", "01886", "USA")).getId();
        userService.register(u);

		given().
			  pathParam("id", addrId.toString()).
		when().
			  get("/addresses/{id}").
		then().
			statusCode(HttpStatus.OK.value()).
			body("number", is("555"),
					"city", is("Westford"));
	}

	@Test
	public void testDeleteAddress() {
		User u = userService.getOrCreate("foouser");
		u.setUsername("foouser");
		AddressId addrId = u.addAddress(new Address("555", "woodbury St", "Westford", "01886", "USA")).getId();
		userService.register(u);

		given().
			pathParam("id", addrId.toString()).
		when().
			delete("/addresses/{id}").
		then().
			statusCode(200);
	}
}
