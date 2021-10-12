/*
 * Copyright (c) 2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.sockshop.spring.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Payment authorization to send back to the Order service.
 */
@Data
@NoArgsConstructor
@Schema(description = "Payment authorization to send back to the Order service")
public class Authorization implements Serializable {
    /**
     * Order identifier.
     */
    @Schema(description = "Order identifier")
    private String orderId;

    /**
     * Time when this payment authorization was created.
     */
    @Schema(description = "Time when this payment authorization was created")
    private LocalDateTime time;

    /**
     * Flag specifying whether the payment was authorized.
     */
    @Schema(description = "Flag specifying whether the payment was authorized")
    private boolean authorised;

    /**
     * Approval or rejection message.
     */
    @Schema(description = "Approval or rejection message")
    private String  message;

    /**
     * Processing error, if any.
     */
    @Schema(description = "Processing error, if any")
    private Err error;

    @Builder
    Authorization(String orderId, LocalDateTime time, boolean authorised, String message, Err error) {
        this.orderId = orderId;
        this.time = time;
        this.authorised = authorised;
        this.message = message;
        this.error = error;
    }

	@JsonIgnore
    public AuthorizationId getId() {
        return new AuthorizationId(orderId, time);
    }
}
