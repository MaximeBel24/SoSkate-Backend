package com.soskate.api.exceptions.spot;

public class SpotNotFoundException extends RuntimeException {
    public SpotNotFoundException(Long id) {
        super("Skatepark with id " + id + " could not be found.");
    }
}
