/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.spring.sockshop.users.controller.converters;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class StringToAddressIdConverter { // implements TypeConverter<String, AddressId> {
//    @Override
//    public Optional<AddressId> convert(String input, Class<AddressId> targetType, ConversionContext context) {
//        if (input != null) {
//            try {
//                return Optional.of(new AddressId(input));
//            } catch (IllegalArgumentException e) {
//                context.reject(e);
//            }
//        }
//        return Optional.empty();
//    }
}
