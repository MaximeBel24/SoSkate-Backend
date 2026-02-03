package com.soskate.api.exceptions.booking;

import lombok.Getter;

@Getter
public class BookingTooSoonException extends BookingException {

    private final int minimumHours;

    public BookingTooSoonException(int minimumHours) {
        super(String.format("Booking must be made at least %d hours in advance", minimumHours));
        this.minimumHours = minimumHours;
    }

}