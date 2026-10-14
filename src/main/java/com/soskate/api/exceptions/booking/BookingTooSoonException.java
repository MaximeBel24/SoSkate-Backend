package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;
import lombok.Getter;

@Getter
public class BookingTooSoonException extends BookingException {

    private final int minimumHours;

    public BookingTooSoonException(int minimumHours) {
        super(ErrorCode.BOOKING_TOO_SOON, String.format("Booking must be made at least %d hours in advance", minimumHours));
        this.minimumHours = minimumHours;
    }

}
