package com.soskate.api.exceptions.booking;

public class CancellationNotAllowedException extends BookingException {

    public CancellationNotAllowedException(String message) {
        super(message);
    }

    public CancellationNotAllowedException() {
        super("L'annulation n'est pas autorisée pour cette réservation");
    }
}