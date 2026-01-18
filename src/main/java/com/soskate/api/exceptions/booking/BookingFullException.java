package com.soskate.api.exceptions.booking;

public class BookingFullException extends BookingException {

    public BookingFullException() {
        super("Ce cours est complet");
    }
}