/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment.repository;

import com.oracle.coherence.examples.sockshop.spring.payment.model.Authorization;
import com.oracle.coherence.examples.sockshop.spring.payment.model.AuthorizationId;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;

import static com.tangosol.util.Filters.equal;

/**
 * An implementation of {@link PaymentRepository}
 * that that uses Coherence as a backend data store.
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Component
public class CoherencePaymentRepository implements PaymentRepository {
    protected final NamedMap<AuthorizationId, Authorization> payments;

    @Inject
    CoherencePaymentRepository(@CoherenceMap("payments") NamedMap<AuthorizationId, Authorization> payments) {
        this.payments = payments;
    }

    @PostConstruct
    void createIndexes() {
        payments.addIndex(Authorization::getOrderId, false, null);
    }

    @Override
    public void saveAuthorization(Authorization auth) {
        payments.put(auth.getId(), auth);
    }

    @Override
    public Collection<? extends Authorization> findAuthorizationsByOrder(String orderId) {
        return payments.values(equal(Authorization::getOrderId, orderId));
    }
}
