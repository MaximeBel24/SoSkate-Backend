package com.soskate.api.exceptions.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Body of every error response sent by the API.
 *
 * @param status    HTTP status, repeated for clients that only read the body
 * @param code      stable error code, see {@link ErrorCode}
 * @param message   human-readable explanation
 * @param timestamp when the error occurred
 * @param errors    invalid fields and their message, only for {@link ErrorCode#VALIDATION_FAILED}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp,
        Map<String, String> errors
) {

    public static ErrorResponse of(ErrorCode code, String message) {
        return of(code, message, null);
    }

    public static ErrorResponse of(ErrorCode code, String message, Map<String, String> errors) {
        return new ErrorResponse(code.getStatus().value(), code.name(), message, Instant.now(), errors);
    }
}
