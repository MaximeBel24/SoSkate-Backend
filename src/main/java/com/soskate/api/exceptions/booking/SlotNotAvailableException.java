package com.soskate.api.exceptions.booking;

public class SlotNotAvailableException extends BookingException {

    public SlotNotAvailableException(String message) {
        super(message);
    }

    public SlotNotAvailableException() {
        super("Ce créneau n'est plus disponible");
    }
}