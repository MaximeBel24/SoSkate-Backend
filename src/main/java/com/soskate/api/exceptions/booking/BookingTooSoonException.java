package com.soskate.api.exceptions.booking;

import lombok.Getter;

@Getter
public class BookingTooSoonException extends BookingException {

    private final int minimumHours;

    public BookingTooSoonException(int minimumHours) {
        super(String.format("La réservation doit être faite au moins %d heures à l'avance", minimumHours));
        this.minimumHours = minimumHours;
    }

}