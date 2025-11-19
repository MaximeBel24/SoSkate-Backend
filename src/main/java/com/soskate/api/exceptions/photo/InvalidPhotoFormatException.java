package com.soskate.api.exceptions.photo;

/**
 * Exception thrown when uploaded file format is invalid or unsupported.
 */
public class InvalidPhotoFormatException extends RuntimeException {

    public InvalidPhotoFormatException(String message) {
        super(message);
    }

    public InvalidPhotoFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}