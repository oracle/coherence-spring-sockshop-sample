/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package io.spring.examples.sockshop.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.logging.Logger;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Implementation of the Catalog Service {@code /catalogue} API.
 */
@Scope(SCOPE_SINGLETON)
@RequestMapping("/catalogue")
@RestController
public class CatalogResource implements CatalogApi {
    private static final Logger LOGGER = Logger.getLogger(CatalogResource.class.getName());

    @Autowired
    private CatalogRepository catalog;

    @Override
    public Collection<? extends Sock> getSocks(String tags, String order, int pageNum, int pageSize) {
        LOGGER.info("CatalogResource.getSocks: size=" + pageSize);
        return catalog.getSocks(tags, order, pageNum, pageSize);
    }

    @Override
    public Count getSockCount(String tags) {
        return new Count(catalog.getSockCount(tags));
    }

    @Override
    public ResponseEntity<Sock> getSock(String sockId) {
        Sock sock = catalog.getSock(sockId);
        return sock == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(sock);
    }

    @Override
    public ResponseEntity<Resource> getImage(String image) {
        Resource imgResource = new ClassPathResource("web/images/" + image);
        return imgResource.exists()
				? ResponseEntity.ok(imgResource)
                : ResponseEntity.notFound().build();
    }

    public static class Count {
        public long size;
        public Object err;

        public Count(long size) {
            this.size = size;
        }
    }
}
