/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(
		info = @Info(
				title = "Spring Sock Shop: Catalog API",
				version = "1.0",
				description = "Allows users to add products they want to buy to a" +
						" shopping catalog, remove them from the cart, and merge the contents" +
						" of one shopping cart into another",
				license = @License(
						name = "The Universal Permissive License (UPL), Version 1.0",
						url = "https://github.com/coherence-community/coherence-sockshop-spring/blob/main/LICENSE.txt"),
				contact = @Contact(
						url = "https://github.com/coherence-community/coherence-sockshop-spring",
						name = "Spring Sock Shop")),
		servers = {@Server(
				url = "https://api.coherence.sockshop.spring.io/",
				description = "Spring Sock Shop implementation with Coherence backend")},
		externalDocs = @ExternalDocumentation(
				description = "Additional Documentation",
				url = "https://github.com/coherence-community/coherence-sockshop-spring/tree/main/catalog"))
@SpringBootApplication
public class CatalogApp {
	public static void main(String[] args) {
		SpringApplication.run(CatalogApp.class, args);
	}

	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("*")
						.allowedHeaders("Content-Type, *");
			}
		};
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		return builder.build();
	}
}
