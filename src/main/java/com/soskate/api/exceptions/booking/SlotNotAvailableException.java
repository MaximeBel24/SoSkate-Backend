package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class SlotNotAvailableException extends BookingException {

    public SlotNotAvailableException(String message) {
        super(ErrorCode.SLOT_NOT_AVAILABLE, message);
    }

    public SlotNotAvailableException() {
        super(ErrorCode.SLOT_NOT_AVAILABLE, "Time slot is no longer available");
    }
}
