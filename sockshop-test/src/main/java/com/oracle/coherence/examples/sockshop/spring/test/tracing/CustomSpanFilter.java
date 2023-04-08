/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.test.tracing;

import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanFilter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSpanFilter implements SpanFilter {
	private volatile List<FinishedSpan> spans = new CopyOnWriteArrayList<>();

	public List<FinishedSpan> getSpans() {
		return spans;
	}

	@Override
	public FinishedSpan map(FinishedSpan finishedSpan) {
		spans.add(finishedSpan);
		return finishedSpan;
	}
}
