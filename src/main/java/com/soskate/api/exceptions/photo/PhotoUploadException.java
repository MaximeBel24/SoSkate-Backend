package com.soskate.api.exceptions.photo;

/**
 * Exception thrown when photo upload fails.
 */
public class PhotoUploadException extends RuntimeException {

    public PhotoUploadException(String message) {
        super(message);
    }

    public PhotoUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}