package com.oracle.coherence.examples.sockshop.spring.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.exporter.FinishedSpan;
import org.springframework.cloud.sleuth.exporter.SpanFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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

	public class CustomSpanFilter implements SpanFilter {
		private List<FinishedSpan> spans = new ArrayList<>();

		@Override
		public boolean isExportable(FinishedSpan span) {
			spans.add(span);
			log.info(span.toString());
			return true;
		}

		public List<FinishedSpan> getSpans() {
			return spans;
		}
	}
}
