package com.soskate.api.exceptions.spot;

/**
 * Exception thrown when a spot is not found.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class SpotNotFoundException extends RuntimeException {

    public SpotNotFoundException(Long id) {
        super(String.format("Spot not found with id: %d", id));
    }

    public SpotNotFoundException(String message) {
        super(message);
    }
}