/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * User data model.
 */
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use=JsonTypeInfo.Id.NAME)
@JsonTypeName("user")
public class WrappedUser extends User {
}
