/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.oracle.coherence.spring.sockshop.users.model.AddressId;

import java.io.IOException;

public class AddressIdDeserializer extends StdDeserializer<AddressId> {

	public AddressIdDeserializer() {
		this(null);
	}

	public AddressIdDeserializer(Class<AddressId> t) {
		super(t);
	}

	@Override
	public AddressId deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);

		String id = node.get("id").asText();
		return new AddressId(id);
	}
}
