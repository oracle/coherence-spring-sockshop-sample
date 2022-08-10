/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Integration tests for {@link UserController}.
 */

public abstract class UserControllerTests {

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
		userService.removeUser("foouser");
		userService.removeUser("baruser");
	}

	protected abstract UserService getUserService();

	@Test
	public void testAuthentication() {
		userService.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
		given().auth().preemptive().basic("foouser", "pass")
			.when()
				.get("/login")
			.then()
				.log().body()
				.assertThat()
					.statusCode(200)
					.body("user.username", is("foouser"))
					.body("user.firstName", is("foo"))
					.body("user.lastName", is("passfoo"))
					.body("user.email", is(nullValue()))
					.body("user.password", is(nullValue()))
					.body("user.id", is("foouser"))
					.body("user", hasKey("_links"))
					.body("user._links", hasKey("customer"))
					.body("user._links", hasKey("self"))
					.body("user._links", hasKey("addresses"))
					.body("user._links", hasKey("cards"))
					.body("user._links.customer.href", endsWith("/customers/foouser"))
					.body("user._links.self.href", endsWith("/customers/foouser"))
					.body("user._links.addresses.href", endsWith("/foouser/addresses"))
					.body("user._links.cards.href", endsWith("/foouser/cards"));
	}

	@Test
	public void testAuthenticationWithWrongPassword() {
		userService.register(new User("foo", "passfoo", "foo@weavesocks.com", "foouser", "pass"));
		given().auth().preemptive().basic("foouser", "wrong").
				when().
				get("/login").
				then().
				assertThat().
				statusCode(401);
	}

	@Test
	public void testRegister() {
		userService.removeUser("baruser");
		given()
				.contentType(JSON)
				.body(new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass"))
				.log().body()
			.when().post("/register")
			.then().log()
			.body().statusCode(200)
			.body("id", is("baruser"));

	}
}
