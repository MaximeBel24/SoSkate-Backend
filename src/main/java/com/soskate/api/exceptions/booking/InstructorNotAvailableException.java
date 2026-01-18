package com.soskate.api.exceptions.booking;

public class InstructorNotAvailableException extends BookingException {

    public InstructorNotAvailableException() {
        super("L'instructeur n'est pas disponible sur ce créneau");
    }
}