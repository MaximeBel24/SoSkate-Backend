package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class BookingModificationNotAllowedException extends BookingException {
    public BookingModificationNotAllowedException(String message) {
        super(ErrorCode.MODIFICATION_NOT_ALLOWED, message);
    }
}
