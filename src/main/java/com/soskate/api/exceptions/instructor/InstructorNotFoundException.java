package com.soskate.api.exceptions.instructor;

/**
 * Exception thrown when an instructor is not found.
 */
public class InstructorNotFoundException extends RuntimeException {

    public InstructorNotFoundException(Long id) {
        super("Instructor not found with id: " + id);
    }

    public InstructorNotFoundException(String message) {
        super(message);
    }
}