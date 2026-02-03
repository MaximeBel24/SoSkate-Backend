package com.soskate.api.exceptions.booking;

public class SlotNotAvailableException extends BookingException {

    public SlotNotAvailableException(String message) {
        super(message);
    }

    public SlotNotAvailableException() {
        super("Time slot is no longer available");
    }
}