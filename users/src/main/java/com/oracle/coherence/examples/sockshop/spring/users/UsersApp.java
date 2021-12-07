/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users;

import com.oracle.coherence.examples.sockshop.spring.users.model.User;
import com.oracle.coherence.spring.configuration.annotation.EnableCoherence;
import com.oracle.coherence.spring.data.config.EnableCoherenceRepositories;
import com.oracle.coherence.examples.sockshop.spring.users.service.UserService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.RestTemplate;

@SecurityScheme(
		name = "basicAuth", // can be set to anything
		type = SecuritySchemeType.HTTP,
		scheme = "basic"
)
@OpenAPIDefinition(
		info = @Info(
				title = "Users",
				version = "1.0",
				description = "Allows user to login, register and retrieval for user, cards and addresses",
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
				url = "https://github.com/coherence-community/coherence-sockshop-spring/tree/main/users"),
		security = @SecurityRequirement(name = "basicAuth"))
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableCoherenceRepositories
public class UsersApp implements ApplicationRunner {

	@Autowired
	private UserService userService;

	@Autowired
	UserDetailsService userDetailsService;

	public static void main(String[] args) {
		SpringApplication.run(UsersApp.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		userService.register(
				new User("Eve", "Berger", "eve_berger@sockshop", "Eve_Berger",
						"eve"));
		userService.register(
				new User("User", "Name", "user@sockshop", "user",
						"password"));
		userService.register(
				new User("User1", "Name1", "user1@sockshop", "user1",
						"password"));
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		return builder.build();
	}
}
