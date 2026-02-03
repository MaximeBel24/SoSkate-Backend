package com.soskate.api.exceptions.spot;

/**
 * Exception levée lorsqu'un spot avec le même nom et adresse existe déjà.
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