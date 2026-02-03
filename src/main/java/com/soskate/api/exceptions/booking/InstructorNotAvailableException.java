package com.soskate.api.exceptions.booking;

public class InstructorNotAvailableException extends BookingException {

    public InstructorNotAvailableException() {
        super("Instructor is not available for this time slot");
    }
}