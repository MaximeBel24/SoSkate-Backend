package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class InstructorNotAvailableException extends BookingException {

    public InstructorNotAvailableException() {
        super(ErrorCode.INSTRUCTOR_NOT_AVAILABLE, "Instructor is not available for this time slot");
    }
}
