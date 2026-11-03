package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class ParticipationNotFoundException extends BookingException {
    public ParticipationNotFoundException(String message) {
        super(ErrorCode.PARTICIPATION_NOT_FOUND, message);
    }
}
