package com.soskate.api.exceptions.instructor;

public class InstructorAlreadyDeletedException extends RuntimeException {
    public InstructorAlreadyDeletedException(String message) {
        super(message);
    }
}
