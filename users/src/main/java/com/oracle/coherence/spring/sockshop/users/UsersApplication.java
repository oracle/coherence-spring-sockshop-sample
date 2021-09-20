/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users;

import com.oracle.coherence.spring.configuration.annotation.EnableCoherence;
import com.oracle.coherence.spring.data.config.EnableCoherenceRepositories;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

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
				url = "https://github.com/coherence-community/coherence-sockshop-spring/tree/main/users"))
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
@EnableCoherence
@EnableCoherenceRepositories
public class UsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}
