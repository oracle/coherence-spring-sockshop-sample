/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI api() {
		return new OpenAPI()
				.info(new Info().title("Spring Sock Shop API")
						.description("Spring Sock Shop sample application")
						.version("v1.0")
						.license(new License().name("Universal Permissive License v 1.0").url("https://oss.oracle.com/licenses/upl")))
				.externalDocs(new ExternalDocumentation()
						.description("Spring Sock Shop GitHub Repo")
						.url("https://github.com/oracle/coherence-spring-sockshop-sample"));
	}
}
