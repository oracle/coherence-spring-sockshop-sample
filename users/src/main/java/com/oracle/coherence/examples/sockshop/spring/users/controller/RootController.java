/*
 * Copyright (c) 2021, 2023, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller;

import com.oracle.coherence.examples.sockshop.spring.users.controller.resource.RootResource;

import com.oracle.coherence.examples.sockshop.spring.users.model.CustomUserEntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class RootController {

	/**
	 * Contains links pointing to controllers backing an entity type (such as streams).
	 */
	private final EntityLinks entityLinks;

	/**
	 * Construct an {@code RootController}.
	 *
	 * @param entityLinks holder of links to controllers and their associated entity types
	 */
	public RootController(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	@GetMapping("/")
	@ResponseStatus(value = HttpStatus.OK)
	public RootResource root(final HttpServletRequest request, final HttpServletResponse response) {
		final RootResource rootResource = new RootResource(1);
		final CustomUserEntityModel userLogin = WebMvcLinkBuilder.methodOn(UserController.class).login(null);
		rootResource.add(WebMvcLinkBuilder.linkTo(userLogin).withRel("user/login"));
		return rootResource;
	}
}
