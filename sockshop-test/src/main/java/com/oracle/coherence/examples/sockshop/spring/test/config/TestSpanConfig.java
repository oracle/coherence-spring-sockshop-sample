/*
 * Copyright (c) 2021, 2023, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.test.config;

import com.oracle.coherence.examples.sockshop.spring.test.tracing.CustomSpanFilter;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanFilter;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test Configuration for Micrometer related tracing tests. Registers a custom {@link SpanFilter} to
 * allow for the introspection of {@link FinishedSpan}s.
 *
 * @author Gunnar Hillert
 */
@Configuration
@Slf4j
public class TestSpanConfig {

	@Bean
	public CustomSpanFilter spanHandler() {
		return new CustomSpanFilter();
	}

	@Bean
	ObservationRegistryCustomizer<ObservationRegistry> skipSecuritySpansFromObservation() {
		return (registry) -> registry.observationConfig().observationPredicate((name, context) ->
				!name.startsWith("spring.security"));
	}
}
