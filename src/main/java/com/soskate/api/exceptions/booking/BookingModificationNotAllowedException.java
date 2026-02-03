package com.soskate.api.exceptions.booking;

public class BookingModificationNotAllowedException extends BookingException {
    public BookingModificationNotAllowedException(String message) {
        super(message);
    }
}
