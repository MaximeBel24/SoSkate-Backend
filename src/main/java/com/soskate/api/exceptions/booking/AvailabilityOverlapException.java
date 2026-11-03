package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class AvailabilityOverlapException extends BookingException {

    public AvailabilityOverlapException() {
        super(ErrorCode.AVAILABILITY_OVERLAP, "Availability overlaps with an existing one");
    }
}
