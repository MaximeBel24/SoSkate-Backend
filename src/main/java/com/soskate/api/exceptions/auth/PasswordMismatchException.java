package com.soskate.api.exceptions.auth;

/**
 * Exception thrown when password and password confirmation do not match.
 */
public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Password and password confirmation do not match");
    }
}