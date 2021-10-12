/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.users.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oracle.coherence.examples.sockshop.spring.users.controller.AddressController;
import com.oracle.coherence.examples.sockshop.spring.users.model.support.AddressIdDeserializer;
import com.oracle.coherence.examples.sockshop.spring.users.model.support.AddressIdSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

/**
 * Representation of an address.
 */
@Data
@NoArgsConstructor
@Schema(description = "User address")
@Relation(collectionRelation = "address", itemRelation = "address")
@JsonPropertyOrder({ "street", "number", "country", "city", "postcode", "id" })
public class Address extends RepresentationModel<Address> implements Serializable {
	/**
	 * The address identifier.
	 */
	@Schema(description = "Address identifier")
	@JsonDeserialize(using = AddressIdDeserializer.class)
	@JsonSerialize(using = AddressIdSerializer.class)
	@JsonProperty("id")
	private AddressId addressId;

	/**
	 * The street number.
	 */
	@Schema(description = "Street number")
	private String number;

	/**
	 * The street name.
	 */
	@Schema(description = "Street name")
	private String street;

	/**
	 * The city name.
	 */
	@Schema(description = "City name")
	private String city;

	/**
	 * The postal code.
	 */
	@Schema(description = "Postal code")
	private String postcode;

	/**
	 * The country name.
	 */
	@Schema(description = "Country name")
	private String country;

	/**
	 * The user this address is associated with, purely for JPA optimization.
	 */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private User user;

	/**
	 * Construct {@code Address} with specified parameters.
	 */
	public Address(String number, String street, String city, String postcode, String country) {
		this.number = number;
		this.street = street;
		this.city = city;
		this.postcode = postcode;
		this.country = country;
	}

	/**
	 * Return the user this address is associated with.
	 *
	 * @return the user this address is associated with
	 */
	public User getUser() {
	return user;
	}

	/**
	 * Set the uer this address belongs to.
	 *
	 * @param user the user to set
	 *
	 * @return this user
	 */
	public Address setUser(User user) {
		this.user = user;
		return this;
	}

	@Override
	public org.springframework.hateoas.Links getLinks() {
		final Link self = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AddressController.class).getAddress(this.getAddressId())).withSelfRel();
		return org.springframework.hateoas.Links.of(self);
	}

}
