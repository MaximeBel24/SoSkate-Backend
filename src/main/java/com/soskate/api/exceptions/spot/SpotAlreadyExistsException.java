package com.soskate.api.exceptions.spot;

import java.math.BigDecimal;

public class SpotAlreadyExistsException extends RuntimeException {
    public SpotAlreadyExistsException(String name, BigDecimal latitude, BigDecimal longitude) {
        super("Skatepark with name : " + name + ", latitude : " + latitude + " and longitude " + longitude + " already exists.");
    }
}
