/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Links extends LinkedHashMap<String, Links.Link> implements Serializable {
	private static final Map<String, String> ENTITY_MAP = Map.of("customer", "customers",
																 "address", "addresses",
																 "card", "cards");

	private Links addLink(String entity, String id) {
		Link link = Link.to(ENTITY_MAP.get(entity), id);
		put(entity, link);
		put("self", link);
		return this;
	}

	private Links addAttrLink(String entity, String id, String attr) {
		Link link = Link.to(ENTITY_MAP.get(entity), id, attr);
		put(attr, link);
		return this;
	}

	public static Links customer(String id) {
		return new Links()
			.addLink("customer", id)
			.addAttrLink("customer", id, "addresses")
			.addAttrLink("customer", id, "cards");
	}

	public static Links address(AddressId id) {
		return new Links().addLink("address", id.toString());
	}

	public static Links card(CardId id) {
		return new Links().addLink("card", id.toString());
	}

	public static class Link implements Serializable {
		public String href;

		public Link() {
		}

		Link(String href) {
			this.href = href;
		}

		static Link to(Object... pathElements) {
			StringBuilder sb = new StringBuilder("http://user");
			for (Object e : pathElements) {
				sb.append('/').append(e);
			}
			return new Link(sb.toString());
		}
	}
}
