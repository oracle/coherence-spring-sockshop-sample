/*
 * Copyright (c) 2021, 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.hateoas.EntityModel;

/**
 * Customized {@link EntityModel} for the {@link User} to tweak the generated JSON output to satisfy the requirements of
 * the Frontend.
 */
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use=JsonTypeInfo.Id.NAME)
@JsonTypeName("user")
public class CustomUserEntityModel extends EntityModel<User> {
	public CustomUserEntityModel(User content) {
		super(content);
	}
}
