package com.johnmartin.coaching.dto.response.common;

import java.util.Optional;

/**
 * A generic Result class that represents either a success with data or a failure with an error.
 *
 * @param <T>
 *            Type of the success data
 */
public record Result<T>(T data, ApiErrorResponse error) {

    /**
     * Factory method for success
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    /**
     * Factory method for failure
     */
    public static <T> Result<T> failure(ApiErrorResponse apiErrorResponse) {
        return new Result<>(null, apiErrorResponse);
    }

    /**
     * Get data wrapped in Optional
     */
    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    public ApiErrorResponse getError() {
        return error;
    }
}
