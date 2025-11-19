package com.soskate.api.exceptions.photo;

/**
 * Exception thrown when a requested photo is not found.
 */
public class PhotoNotFoundException extends RuntimeException {

    public PhotoNotFoundException(Long photoId) {
        super("Photo not found with ID: " + photoId);
    }

    public PhotoNotFoundException(String message) {
        super(message);
    }
}
