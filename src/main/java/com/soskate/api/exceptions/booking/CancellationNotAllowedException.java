package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class CancellationNotAllowedException extends BookingException {

    public CancellationNotAllowedException(String message) {
        super(ErrorCode.CANCELLATION_NOT_ALLOWED, message);
    }

    public CancellationNotAllowedException() {
        super(ErrorCode.CANCELLATION_NOT_ALLOWED, "Cancellation is not allowed for this booking");
    }
}
