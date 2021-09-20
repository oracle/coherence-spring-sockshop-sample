/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller.converters;

import com.oracle.coherence.spring.sockshop.users.model.CardId;
import org.springframework.beans.TypeConverter;

import java.util.Optional;

public class StringToCardIdConverter { // implements TypeConverter<String, CardId> {
//    @Override
//    public Optional<CardId> convert(String input, Class<CardId> targetType, ConversionContext context) {
//        if (input != null) {
//            try {
//                return Optional.of(new CardId(input));
//            } catch (IllegalArgumentException e) {
//                context.reject(e);
//            }
//        }
//        return Optional.empty();
//    }
}
