/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.carts;

import com.oracle.coherence.examples.sockshop.spring.carts.model.Item;
import com.oracle.coherence.examples.sockshop.spring.test.config.TestSpanConfig;
import com.oracle.coherence.examples.sockshop.spring.test.tracing.CustomSpanFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import io.micrometer.tracing.exporter.FinishedSpan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StringUtils;

import java.util.List;

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
				"coherence.localhost=127.0.0.1",
				"coherence.ttl=0",
				"java.net.preferIPv4Stack=true",
				"coherence.wka=127.0.0.1",
				"management.tracing.sampling.probability=1.0",
				"coherence.metrics.http.enabled=true"
		}
)
@AutoConfigureObservability
@AutoConfigureWebTestClient
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSpanConfig.class)
@Slf4j
public class CartMetricsTests {

	public static final int COHERENCE_METRICS_PORT = 9612;
	public static final String COHERENCE_METRICS_URL = "http://localhost:" + COHERENCE_METRICS_PORT + "/metrics";
	public static final String CART_URL = "/carts/{cartId}/items";
	public static final String CART_ID = "C1";

	@Autowired
	protected WebTestClient webTestClient;

	@Autowired
	private CustomSpanFilter spanHandler;

	@Test
	@Order(1)
	void addItemToCart() {
		webTestClient.post()
				.uri(CART_URL, CART_ID)
				.bodyValue(new Item("X1", 0, 10f))
				.exchange()
				.expectStatus().isCreated()
				.expectBody().jsonPath("itemId").isEqualTo("X1");
	}

	@Test
	@Order(2)
	void verifyCoherenceMetrics() {
		this.webTestClient.get()
				.uri(COHERENCE_METRICS_URL + "/Coherence.Cache.Size?name=carts&tier=back")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
					.consumeWith(System.out::println)
					.jsonPath("$.length()").isEqualTo(1)
					.jsonPath("$[0].tags.name").isEqualTo("carts")
					.jsonPath("$[0].value").isEqualTo(1);
	}

	@Test
	@Order(3)
	void verifyMicrometerTraces() {
		await().untilAsserted(() ->
				assertThat(this.spanHandler.getSpans())
						.hasSize(1));

		final List<FinishedSpan> spans = this.spanHandler.getSpans();
		log.info("\n" + StringUtils.collectionToDelimitedString(spans, "\n"));

		assertThat(spans)
				.extracting(finishedSpan -> finishedSpan.getTags().get("method"))
				.containsExactlyInAnyOrder("POST");
		assertThat(spans)
				.extracting(finishedSpan -> finishedSpan.getTags().get("uri"))
				.containsExactlyInAnyOrder("/carts/{customerId}/items");
	}
}
