package com.soskate.api.exceptions.spot;

public class SpotDataRetrievalException extends RuntimeException {
    public SpotDataRetrievalException(Exception e) {
        super("Could not retrieve skatepark data", e);
    }
}
