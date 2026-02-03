package com.soskate.api.exceptions.instructor;

/**
 * Exception thrown when an operation cannot be performed due to
 * the current state of an instructor account.
 *
 * <p>Examples:</p>
 * <ul>
 *   <li>Attempting to resend invitation to an already activated account</li>
 *   <li>Attempting to reactivate a non-suspended account</li>
 *   <li>Attempting to activate an already active account</li>
 * </ul>
 *
 * <p>This exception results in HTTP 409 Conflict response.</p>
 */
public class InvalidAccountStateException extends RuntimeException {

    public InvalidAccountStateException(String message) {
        super(message);
    }
}
