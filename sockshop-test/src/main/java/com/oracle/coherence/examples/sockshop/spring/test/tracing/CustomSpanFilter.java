/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.test.tracing;

import org.springframework.cloud.sleuth.exporter.FinishedSpan;
import org.springframework.cloud.sleuth.exporter.SpanFilter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSpanFilter implements SpanFilter {
	private volatile List<FinishedSpan> spans = new CopyOnWriteArrayList<>();

	@Override
	public synchronized boolean isExportable(FinishedSpan span) {
		spans.add(span);
		return false;
	}

	public List<FinishedSpan> getSpans() {
		return spans;
	}

}
