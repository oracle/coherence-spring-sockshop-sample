/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller.resource;

import org.springframework.hateoas.RepresentationModel;

public class RootResource extends RepresentationModel<RootResource> {

	private Integer apiVersion;

	public RootResource() {
	}

	public RootResource(Integer apiVersion) {
		this.apiVersion = apiVersion;
	}

	public Integer getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(Integer apiVersion) {
		this.apiVersion = apiVersion;
	}
}
