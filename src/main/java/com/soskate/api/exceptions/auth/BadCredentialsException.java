package com.soskate.api.exceptions.auth;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when login credentials are invalid.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class BadCredentialsException extends ApiException {

    public BadCredentialsException(String message) {
        super(ErrorCode.BAD_CREDENTIALS, message);
    }

    public BadCredentialsException() {
        super(ErrorCode.BAD_CREDENTIALS, "Invalid email or password");
    }
}
