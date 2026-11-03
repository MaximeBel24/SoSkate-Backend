package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class AvailabilityNotFoundException extends BookingException {

    public AvailabilityNotFoundException(String message) {
        super(ErrorCode.AVAILABILITY_NOT_FOUND, message);
    }
}
