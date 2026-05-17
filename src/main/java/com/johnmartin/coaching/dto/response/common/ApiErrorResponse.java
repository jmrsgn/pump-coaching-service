package com.johnmartin.coaching.dto.response.common;

import java.time.Instant;

public record ApiErrorResponse(int status, String error, String message, Instant timestamp) {

    public ApiErrorResponse(int status, String error, String message) {
        this(status, error, message, Instant.now());
    }
}
