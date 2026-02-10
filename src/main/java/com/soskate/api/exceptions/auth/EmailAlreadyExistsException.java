package com.soskate.api.exceptions.auth;

/**
 * Exception thrown when a user attempts to register with an email that already exists.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructor with a custom message.
     *
     * @param message the error message
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor with email as parameter.
     *
     * @param email the email that already exists
     */
    public EmailAlreadyExistsException(String email, boolean isEmail) {
        super(String.format("An account with email '%s' already exists", email));
    }
}
