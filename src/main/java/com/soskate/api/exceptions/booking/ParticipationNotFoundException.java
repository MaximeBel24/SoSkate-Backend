package com.soskate.api.exceptions.booking;

public class ParticipationNotFoundException extends BookingException {
    public ParticipationNotFoundException(String participationNonTrouvee) {
        super(participationNonTrouvee);
    }
}
