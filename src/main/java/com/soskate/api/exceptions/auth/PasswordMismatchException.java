package com.soskate.api.exceptions.auth;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when password and password confirmation do not match.
 */
public class PasswordMismatchException extends ApiException {

    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH, "Password and password confirmation do not match");
    }
}
