/*
 * Copyright (c) 2021, 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
		properties = {
				"coherence.localhost=127.0.0.1",
				"coherence.ttl=0",
				"java.net.preferIPv4Stack=true",
				"coherence.wka=127.0.0.1"
		}
)
@DirtiesContext
class UsersApplicationTests {

	@Test
	void contextLoads() {
	}

}
