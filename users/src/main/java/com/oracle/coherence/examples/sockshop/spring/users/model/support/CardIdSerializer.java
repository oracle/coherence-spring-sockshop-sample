/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.oracle.coherence.examples.sockshop.spring.users.model.CardId;

import java.io.IOException;

public class CardIdSerializer extends StdSerializer<CardId> {

	public CardIdSerializer() {
		this(null);
	}

	public CardIdSerializer(Class<CardId> t) {
		super(t);
	}

	@Override
	public void serialize(CardId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeString(value.toString());
	}
}
