package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

import java.time.LocalDate;

public class BookingTooSoonException extends BookingException {

    public BookingTooSoonException(LocalDate firstOpenDate) {
        super(ErrorCode.BOOKING_TOO_SOON, "Lessons can be booked from " + firstOpenDate);
    }

}
