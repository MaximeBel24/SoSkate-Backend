package com.soskate.api.exceptions.booking;

import com.soskate.api.exceptions.common.ErrorCode;

public class InstructorNotAtSpotException extends BookingException {

    public InstructorNotAtSpotException() {
        super(ErrorCode.INSTRUCTOR_NOT_AT_SPOT, "Instructor does not teach at this spot");
    }
}
