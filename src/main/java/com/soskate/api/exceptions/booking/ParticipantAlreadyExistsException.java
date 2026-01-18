package com.soskate.api.exceptions.booking;

public class ParticipantAlreadyExistsException extends BookingException {

    public ParticipantAlreadyExistsException() {
        super("Ce client est déjà inscrit à ce cours");
    }
}