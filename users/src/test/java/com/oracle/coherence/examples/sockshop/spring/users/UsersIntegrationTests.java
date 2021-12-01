/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users;

import com.oracle.coherence.examples.sockshop.spring.test.config.TestSpanConfig;
import com.oracle.coherence.examples.sockshop.spring.test.tracing.CustomSpanFilter;
import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.exporter.FinishedSpan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to ensure that
 *
 * <ul>
 *   <li>Users are created successfully
 *   <li>Coherence metrics are properly exposed when
 *       property {@code coherence.metrics.http.enabled} is set to {@code true}.
 *   <li>Spring Cloud Sleuth Traces are being generated
 * </ul>
 * Coherence metrics are properly exposed when
 * property {@code coherence.metrics.http.enabled} is set to {@code true}.
 *
 * @author Gunnar Hillert
 */
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"coherence.metrics.http.enabled=true"
		}
)
@Import(TestSpanConfig.class)
@AutoConfigureWebTestClient
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UsersIntegrationTests {

	public static final int COHERENCE_METRICS_PORT = 9612;
	public static final String COHERENCE_METRICS_URL = "http://localhost:" + COHERENCE_METRICS_PORT + "/metrics";
	public static final String USER_REGISTRATION_URL = "/register";
	public static final String CUSTOMER_URL = "/customers";

	@Autowired
	protected WebTestClient webTestClient;

	@Autowired
	private CustomSpanFilter spanHandler;

	@Test
	@Order(1)
	void registerUser() {
		webTestClient.post()
				.uri(USER_REGISTRATION_URL)
				.bodyValue(new User("bar", "passbar", "bar@weavesocks.com", "baruser", "pass"))
				.exchange()
				.expectStatus().isOk()
				.expectBody().jsonPath("id").isEqualTo("baruser");
	}

	@Test
	@Order(2)
	void verifyCustomers() {
		webTestClient.get()
				.uri(CUSTOMER_URL)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.consumeWith(System.out::println)
					.jsonPath("_embedded.customer.length()").isEqualTo(4);
	}

	@Test
	@Order(3)
	void verifyCoherenceMetrics() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL + "/Coherence.Cache.Size?name=users&tier=back")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.consumeWith(System.out::println)
					.jsonPath("$.length()").isEqualTo(1)
					.jsonPath("$[0].tags.name").isEqualTo("users")
					.jsonPath("$[0].value").isEqualTo(4);
	}

	@Test
	@Order(4)
	void verifySpringCloudSleuthTraces() {
		final List<FinishedSpan> spans = this.spanHandler.getSpans();
		assertThat(spans)
				.hasSize(2)
				.extracting(FinishedSpan::getName)
						.containsExactlyInAnyOrder("POST /register", "GET /customers");
	}
}
