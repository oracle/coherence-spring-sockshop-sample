/*
 * Copyright (c) 2021, 2025 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.config;

import java.util.Optional;
import org.springdoc.core.providers.HateoasHalProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.hateoas.HateoasProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * See: https://github.com/springdoc/springdoc-openapi/issues/2954
 *      https://github.com/springdoc/springdoc-openapi/issues/3005
 */
@Configuration
public class HateoasHalProviderWorkaroundConfiguration
    {
    @Value("${spring.hateoas.use-hal-as-default-json-media-type:true}")
    private boolean useHalAsDefaultJsonMediaType;

    static class HateoasHalProviderWorkaround
            extends HateoasHalProvider
        {
        private final boolean useHalAsDefaultJsonMediaType;

        public HateoasHalProviderWorkaround(Optional<HateoasProperties> hateoasPropertiesOptional, ObjectMapperProvider objectMapperProvider, boolean useHalAsDefaultJsonMediaType)
            {
            super(hateoasPropertiesOptional, objectMapperProvider);
            this.useHalAsDefaultJsonMediaType = useHalAsDefaultJsonMediaType;
            }

        @Override
        public boolean isHalEnabled()
            {
            return useHalAsDefaultJsonMediaType;
            }
        }

    @Bean
    HateoasHalProvider hateoasHalProvider(Optional<HateoasProperties> hateoasPropertiesOptional, ObjectMapperProvider objectMapperProvider)
        {
        return new HateoasHalProviderWorkaround(hateoasPropertiesOptional, objectMapperProvider, useHalAsDefaultJsonMediaType);
        }
    }
