package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.instructor.InstructorAlreadyDeletedException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.instructor.InvalidAccountStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for instructor-related exceptions.
 */
@Slf4j
@RestControllerAdvice
public class InstructorExceptionHandler {

    @ExceptionHandler(InstructorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInstructorNotFound(InstructorNotFoundException ex) {
        log.warn("Instructor not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "INSTRUCTOR_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAccountStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountState(InvalidAccountStateException ex) {
        log.warn("Invalid account state: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "INVALID_ACCOUNT_STATE", ex.getMessage()));
    }

    @ExceptionHandler(InstructorAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleInstructorAlreadyDeleted(InstructorAlreadyDeletedException ex) {
        log.warn("Instructor already deleted: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "INSTRUCTOR_ALREADY_DELETED", ex.getMessage()));
    }
}
