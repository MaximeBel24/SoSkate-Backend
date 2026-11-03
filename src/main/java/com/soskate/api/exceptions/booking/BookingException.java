package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Base class for booking and availability errors.
 * <p>
 * Thrown directly, it reports a generic {@link ErrorCode#BOOKING_ERROR};
 * subclasses report their own, more precise code.
 */
public class BookingException extends ApiException {

    public BookingException(String message) {
        super(ErrorCode.BOOKING_ERROR, message);
    }

    protected BookingException(ErrorCode code, String message) {
        super(code, message);
    }
}
