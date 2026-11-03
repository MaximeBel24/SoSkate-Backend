package com.soskate.api.exceptions.instructor;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when an instructor is not found.
 */
public class InstructorNotFoundException extends ApiException {

    public InstructorNotFoundException(Long id) {
        super(ErrorCode.INSTRUCTOR_NOT_FOUND, "Instructor not found with id: " + id);
    }

    public InstructorNotFoundException(String message) {
        super(ErrorCode.INSTRUCTOR_NOT_FOUND, message);
    }
}
