package com.soskate.api.exceptions.spot;

/**
 * Exception thrown when a spot with the same name and address already exists.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class DuplicateSpotException extends RuntimeException {

    public DuplicateSpotException(String name, String address) {
        super(String.format("Spot with name '%s' already exists at address '%s'", name, address));
    }

    public DuplicateSpotException(String message) {
        super(message);
    }
}