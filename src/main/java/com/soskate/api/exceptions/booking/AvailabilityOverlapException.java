package com.soskate.api.exceptions.booking;

public class AvailabilityOverlapException extends BookingException {

    public AvailabilityOverlapException() {
        super("Cette disponibilité chevauche une disponibilité existante");
    }
}