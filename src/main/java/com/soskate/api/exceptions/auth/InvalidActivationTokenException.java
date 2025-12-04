package com.soskate.api.exceptions.auth;

/**
 * Exception thrown when an activation token is invalid or expired.
 */
public class InvalidActivationTokenException extends RuntimeException {

    public InvalidActivationTokenException() {
        super("Invalid or expired activation token");
    }

    public InvalidActivationTokenException(String message) {
        super(message);
    }
}