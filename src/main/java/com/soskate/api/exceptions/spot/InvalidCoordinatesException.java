package com.soskate.api.exceptions.spot;

/**
 * Exception thrown when GPS coordinates are invalid.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class InvalidCoordinatesException extends RuntimeException {

    public InvalidCoordinatesException(String message) {
        super(message);
    }

    public InvalidCoordinatesException() {
        super("Invalid GPS coordinates provided");
    }
}