package com.soskate.api.exceptions.auth;

/**
 * Exception thrown when login credentials are invalid.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException() {
        super("Invalid email or password");
    }
}