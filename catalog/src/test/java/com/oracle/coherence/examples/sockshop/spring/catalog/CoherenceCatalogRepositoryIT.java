/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for Coherence repository implementation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CoherenceCatalogRepositoryIT extends CatalogRepositoryTest {

    @BeforeAll
    static void start() {
//      disable global tracing so we can start server in multiple test suites
        System.setProperty("tracing.global", "false");
    }

}