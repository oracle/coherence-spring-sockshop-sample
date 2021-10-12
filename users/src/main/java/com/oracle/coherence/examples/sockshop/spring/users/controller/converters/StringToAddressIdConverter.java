/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.controller.converters;

import com.oracle.coherence.examples.sockshop.spring.users.model.AddressId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToAddressIdConverter implements Converter<String, AddressId> {
	@Override
	public AddressId convert(String source) {
		if (source != null) {
			return new AddressId(source);
		}
		else {
			return null;
		}
	}
}
