/*
 * Copyright (c) 2021, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.orders;

import java.net.URI;
import java.util.List;

import com.oracle.coherence.examples.sockshop.spring.orders.controller.support.NewOrderRequest;
import com.oracle.coherence.examples.sockshop.spring.test.config.TestSpanConfig;
import com.oracle.coherence.examples.sockshop.spring.test.tracing.CustomSpanFilter;
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
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test to ensure Coherence metrics are properly exposed when
 * property {@code coherence.metrics.http.enabled} is set to {@code true}.
 *
 * @author Gunnar Hillert
 */
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"coherence.metrics.http.enabled=true",
				"spring.zipkin.enabled=true",
				"spring.sleuth.sampler.probability=1.0"
		}
)
@AutoConfigureWebTestClient
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSpanConfig.class)
@Slf4j
public class OrdersMetricsTests {

	public static final int COHERENCE_METRICS_PORT = 9612;
	public static final String COHERENCE_METRICS_URL = "http://localhost:" + COHERENCE_METRICS_PORT + "/metrics";
	public static final String ORDERS_URL = "/orders";

	@Autowired
	protected WebTestClient webTestClient;

	@Autowired
	private CustomSpanFilter spanHandler;

	@Test
	@Order(1)
	void createOrder() {
		String baseUri = "";
		NewOrderRequest req = NewOrderRequest.builder()
				.customer(URI.create(baseUri + "/customers/homer"))
				.address(URI.create(baseUri + "/addresses/homer:1"))
				.card(URI.create(baseUri + "/cards/homer:1234"))
				.items(URI.create(baseUri + "/carts/homer/items"))
				.build();

		webTestClient.post()
				.uri(ORDERS_URL)
				.bodyValue(req)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
					.jsonPath("total").isEqualTo(14.0f)
					.jsonPath("status").isEqualTo("CREATED");
	}

	@Test
	@Order(2)
	void verifyCoherenceMetrics() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL + "/Coherence.Cache.Size?name=orders&tier=back")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.consumeWith(System.out::println)
					.jsonPath("$.length()").isEqualTo(1)
					.jsonPath("$[0].tags.name").isEqualTo("orders")
					.jsonPath("$[0].value").isEqualTo(1);
	}

	@Test
	@Order(3)
	void verifySpringCloudSleuthTraces() {
		await().untilAsserted(() ->
				assertThat(this.spanHandler.getSpans())
						.hasSize(4));

		final List<FinishedSpan> spans = this.spanHandler.getSpans();
		log.info("\n" + StringUtils.collectionToDelimitedString(spans, "\n"));
		assertThat(spans)
				.hasSize(4)
				.extracting(FinishedSpan::getName)
				.containsExactlyInAnyOrder("on-order-created", "POST /orders", "on-order-created", "on-order-created");
	}
}
