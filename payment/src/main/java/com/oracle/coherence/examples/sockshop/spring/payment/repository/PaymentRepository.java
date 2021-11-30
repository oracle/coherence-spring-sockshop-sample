/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.repository;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Authorization;

import java.util.Collection;

import javax.validation.constraints.NotNull;

/**
 * A repository interface that should be implemented by
 * the various data store integrations.
 */
public interface PaymentRepository {
    /**
     * Save payment authorization details.
     *
     * @param auth payment authorization details
     */
    void saveAuthorization(Authorization auth);

    /**
     * Find all authorizations for the specified order.
     *
     * @param orderId the order identifier to find the authorizations for
     *
     * @return all authorizations for the specified order; never {@code null}
     */
    @NotNull Collection<? extends Authorization> findAuthorizationsByOrder(String orderId);
}
