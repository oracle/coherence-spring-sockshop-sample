/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog;

import java.util.Collection;
import java.util.Set;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface    CatalogRepository {
    /**
     * Return socks from the catalog based on the specified criteria.
     *
     * @param tags     a comma-separated list of tags; can be {@code null}
     * @param order    the name of the property to order the results by;
     *                 can be {@code price} or {@code name}
     * @param pageNum  the page of results to return
     * @param pageSize the maximum number of results to return
     *
     * @return a collection of {@code Sock}s based on the specified criteria
     */
    Collection<? extends Sock> getSocks(String tags, String order, int pageNum, int pageSize);

    /**
     * Return a {@code Sock} with the specified identifier.
     *
     * @param sockId the sock identifier
     *
     * @return a {@code Sock} with the specified identifier
     */
    Sock getSock(String sockId);

    /**
     * Return the number of socks in the catalog based on the specified criteria.
     *
     * @param tags a comma-separated list of tags; can be {@code null}
     *
     * @return the number of socks in the catalog for the specified criteria
     */
    long getSockCount(String tags);

    /**
     * Return all tags from the catalog.
     *
     * @return all tags from the catalog
     */
    Set<String> getTags();
}
