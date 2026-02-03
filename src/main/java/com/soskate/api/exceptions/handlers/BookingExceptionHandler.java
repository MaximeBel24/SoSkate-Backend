package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.booking.*;
import com.soskate.api.exceptions.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for booking-related exceptions.
 */
@Slf4j
@RestControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSlotNotAvailable(SlotNotAvailableException ex) {
        log.warn("Slot not available: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "SLOT_NOT_AVAILABLE", ex.getMessage()));
    }

    @ExceptionHandler(BookingTooSoonException.class)
    public ResponseEntity<ErrorResponse> handleBookingTooSoon(BookingTooSoonException ex) {
        log.warn("Booking too soon: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "BOOKING_TOO_SOON", ex.getMessage()));
    }

    @ExceptionHandler(InstructorNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleInstructorNotAvailable(InstructorNotAvailableException ex) {
        log.warn("Instructor not available: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "INSTRUCTOR_NOT_AVAILABLE", ex.getMessage()));
    }

    @ExceptionHandler(InstructorNotAtSpotException.class)
    public ResponseEntity<ErrorResponse> handleInstructorNotAtSpot(InstructorNotAtSpotException ex) {
        log.warn("Instructor not at spot: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "INSTRUCTOR_NOT_AT_SPOT", ex.getMessage()));
    }

    @ExceptionHandler(CancellationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleCancellationNotAllowed(CancellationNotAllowedException ex) {
        log.warn("Cancellation not allowed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "CANCELLATION_NOT_ALLOWED", ex.getMessage()));
    }

    @ExceptionHandler(AvailabilityOverlapException.class)
    public ResponseEntity<ErrorResponse> handleAvailabilityOverlap(AvailabilityOverlapException ex) {
        log.warn("Availability overlap: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "AVAILABILITY_OVERLAP", ex.getMessage()));
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAvailabilityNotFound(AvailabilityNotFoundException ex) {
        log.warn("Availability not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "AVAILABILITY_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BookingModificationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleBookingModificationNotAllowed(BookingModificationNotAllowedException ex) {
        log.warn("Modification not allowed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "MODIFICATION_NOT_ALLOWED", ex.getMessage()));
    }

    @ExceptionHandler(ParticipationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleParticipationNotFound(ParticipationNotFoundException ex) {
        log.warn("Participation not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "PARTICIPATION_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorResponse> handleBookingException(BookingException ex) {
        log.warn("Booking error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "BOOKING_ERROR", ex.getMessage()));
    }
}
