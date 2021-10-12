/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders.controller.support;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.oracle.coherence.examples.sockshop.spring.orders.model.Order;
import lombok.Data;

/**
 * Helper class to create {@code _links} structure for the
 * {@link Order}.
 */
public class Links extends LinkedHashMap<String, Links.Link> implements Serializable {
    private static Map<String, String> ENTITY_MAP = Map.of("order", "orders");

    /**
     * Add link to the specified entity.
     *
     * @param entity the entity type to add link for
     * @param id     the entity identifier
     *
     * @return this {@code Links} structure
     */
    private Links addLink(String entity, String id) {
        Link link = Link.to(ENTITY_MAP.get(entity), id);
        put("self", link);
        return this;
    }

    /**
     * Create {@code Links} for the specified order.
     *
     * @param id the order identifier
     *
     * @return the {@code Links} for the specified order
     */
    public static Links order(String id) {
        return new Links()
            .addLink("order", id);
    }

    /**
     * Single link representation.
     */
    @Data
    public static class Link implements Serializable {
        /**
         * Link's {@code href} value.
         */
        private String href;

        /**
         * Construct {@code Link} instance.
         *
         * @param href link's {@code href} value
         */
        Link(String href) {
            this.href = href;
        }

        /**
         * Factory method for link(s).
         *
         * @param pathElements path elements to append to base path
         *
         * @return fully constructed {@code Link}
         */
        static Link to(Object... pathElements) {
            StringBuilder sb = new StringBuilder("http://orders");
            for (Object e : pathElements) {
                sb.append('/').append(e);
            }
            return new Link(sb.toString());
        }
    }
}
