package com.soskate.api.exceptions.booking;

public class InstructorNotAtSpotException extends BookingException {

    public InstructorNotAtSpotException() {
        super("Instructor does not teach at this spot");
    }
}