package com.soskate.api.exceptions.common;

import lombok.Getter;

/**
 * Base class of every error the API reports to its clients on purpose.
 * <p>
 * The {@link ErrorCode} decides the HTTP status and the {@code code} of the response.
 * The message is sent to the client as is, except for 5xx codes, whose message is only logged.
 */
@Getter
public class ApiException extends RuntimeException {

    private final ErrorCode code;

    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
