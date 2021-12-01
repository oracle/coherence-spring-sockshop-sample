/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.test.config;

import com.oracle.coherence.examples.sockshop.spring.test.tracing.CustomSpanFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.exporter.FinishedSpan;
import org.springframework.cloud.sleuth.exporter.SpanFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test Configuration for Spring Cloud Sleuth related tracing tests. Registers a custom {@link SpanFilter} to
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

}
