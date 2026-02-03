package com.soskate.api.exceptions.booking;

public class AvailabilityOverlapException extends BookingException {

    public AvailabilityOverlapException() {
        super("Availability overlaps with an existing one");
    }
}