package com.soskate.api.exceptions.booking;

public class InstructorNotAtSpotException extends BookingException {

    public InstructorNotAtSpotException() {
        super("L'instructeur n'enseigne pas sur ce spot");
    }
}