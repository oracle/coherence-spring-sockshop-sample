/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.catalog.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.oracle.coherence.examples.sockshop.spring.catalog.model.Sock;
import com.oracle.coherence.spring.configuration.annotation.CoherenceMap;
import com.tangosol.net.NamedMap;
import com.tangosol.util.Aggregators;
import com.tangosol.util.Filter;
import com.tangosol.util.Filters;
import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.UniversalExtractor;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.LimitFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * An implementation of {@link CatalogRepository}
 * that that uses Coherence as a backend data store.
 */
@Scope(SCOPE_SINGLETON)
@Repository
public class CoherenceCatalogRepository implements CatalogRepository {
    private static final Logger LOGGER = Logger.getLogger(CoherenceCatalogRepository.class.getName());

	@CoherenceMap
    private NamedMap<String, Sock> socks;

    private static Comparator<Sock> PRICE_COMPARATOR = new ExtractorComparator<>(new UniversalExtractor<Sock, Float>("price"));
    private static Comparator<Sock> NAME_COMPARATOR  = new ExtractorComparator<>(new UniversalExtractor<Sock, String>("name"));


    @Override
    public Collection<? extends Sock> getSocks(String tags, String order, int pageNum, int pageSize) {
        Comparator<Sock> comparator = "price".equals(order)
                ? PRICE_COMPARATOR
                : "name".equals(order)
                        ? NAME_COMPARATOR
                        : null;

        LimitFilter<Sock> filter = new LimitFilter<>(createTagsFilter(tags), pageSize);
        filter.setPage(pageNum -1);
        return socks.values(filter, comparator);
    }

    @Override
    public Sock getSock(String sockId) {
        return socks.get(sockId);
    }

    @Override
    public long getSockCount(String tags) {
        return socks.aggregate(createTagsFilter(tags), Aggregators.count());
    }

    @Override
    public Set<String> getTags() {
        Collection<Sock> result = socks.values();
        return Arrays.stream(result.toArray())
                .flatMap(sock -> ((Sock) sock).getTag().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Initialize this repository.
     */
    @PostConstruct
    void init() {
        loadData();
    }

    /**
     * Load test data into this repository.
     */
    public CatalogRepository loadData() {
        if (socks.isEmpty()) {
            loadSocksFromJson(Sock.class)
                    .forEach(sock -> socks.put(sock.getId(), sock));
        }
        return this;
    }

    /**
     * Load socks from a JSON file.
     *
     * @param asClass the class to load data as; must be {@link Sock}
     *                or its subclass
     * @param <T>     the type to load data as
     * @return a list of socks
     */
    protected <T extends Sock> List<T> loadSocksFromJson(Class<T> asClass) {
        ObjectMapper mapper = new ObjectMapper();

        CollectionType javaType = mapper.getTypeFactory()
                .constructCollectionType(List.class, Sock.class);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("data.json")) {
            return mapper.readValue(in, javaType);
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            return Collections.emptyList();
        }
    }

    private Filter<Sock> createTagsFilter(String tags) {
        Filter<Sock> filter = AlwaysFilter.INSTANCE();
        if (tags != null && !"".equals(tags)) {
            String[] aTags = tags.split(",");
            filter = Filters.containsAny(Sock::getTag, aTags);
        }
        return filter;
    }
}