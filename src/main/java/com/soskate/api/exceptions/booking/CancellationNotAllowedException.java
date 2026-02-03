package com.soskate.api.exceptions.booking;

public class CancellationNotAllowedException extends BookingException {

    public CancellationNotAllowedException(String message) {
        super(message);
    }

    public CancellationNotAllowedException() {
        super("Cancellation is not allowed for this booking");
    }
}