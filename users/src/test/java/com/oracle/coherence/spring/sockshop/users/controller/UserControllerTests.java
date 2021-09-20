/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller;

import com.oracle.coherence.spring.sockshop.users.model.User;
import com.oracle.coherence.spring.sockshop.users.service.UserService;
import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import static org.hamcrest.Matchers.is;

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
		given().auth().preemptive().basic("foouser", "pass").
		when().
			get("/login").
		then().
			assertThat().
			statusCode(200);
	}

	@Test
	public void testRegister() {
		userService.removeUser("baruser");
		given().
			contentType(JSON).
			body(new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass")).
		when().
			post("/register").
		then().
			statusCode(200).
			body("id", is("baruser"));
	}
}
