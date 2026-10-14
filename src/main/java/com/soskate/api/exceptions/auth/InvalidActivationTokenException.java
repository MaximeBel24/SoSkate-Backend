package com.soskate.api.exceptions.auth;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when an activation token is invalid or expired.
 */
public class InvalidActivationTokenException extends ApiException {

    public InvalidActivationTokenException() {
        super(ErrorCode.INVALID_TOKEN, "Invalid or expired activation token");
    }

    public InvalidActivationTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, message);
    }
}
