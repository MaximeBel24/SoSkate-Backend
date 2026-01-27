package com.soskate.api.exceptions.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard error response for the API.
 * Used by all exception handlers for consistent error format.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    /**
     * Creates an ErrorResponse without an error code.
     */
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, null, message, LocalDateTime.now());
    }

    /**
     * Creates an ErrorResponse with an error code.
     */
    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message, LocalDateTime.now());
    }
}
