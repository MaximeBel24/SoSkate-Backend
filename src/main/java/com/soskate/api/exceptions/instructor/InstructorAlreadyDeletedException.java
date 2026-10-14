package com.soskate.api.exceptions.instructor;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

public class InstructorAlreadyDeletedException extends ApiException {
    public InstructorAlreadyDeletedException(String message) {
        super(ErrorCode.INSTRUCTOR_ALREADY_DELETED, message);
    }
}
