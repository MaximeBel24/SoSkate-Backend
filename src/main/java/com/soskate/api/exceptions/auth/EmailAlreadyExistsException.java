package com.soskate.api.exceptions.auth;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when a user attempts to register with an email that already exists.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class EmailAlreadyExistsException extends ApiException {

    /**
     * Constructor with a custom message.
     *
     * @param message the error message
     */
    public EmailAlreadyExistsException(String message) {
        super(ErrorCode.EMAIL_EXISTS, message);
    }

    /**
     * Constructor with email as parameter.
     *
     * @param email the email that already exists
     */
    public EmailAlreadyExistsException(String email, boolean isEmail) {
        super(ErrorCode.EMAIL_EXISTS, String.format("An account with email '%s' already exists", email));
    }
}
