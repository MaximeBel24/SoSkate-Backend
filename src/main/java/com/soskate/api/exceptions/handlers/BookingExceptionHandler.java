package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.booking.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler(SlotNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleSlotNotAvailable(SlotNotAvailableException ex) {
        return buildResponse(HttpStatus.CONFLICT, "SLOT_NOT_AVAILABLE", ex.getMessage());
    }

    @ExceptionHandler(BookingTooSoonException.class)
    public ResponseEntity<Map<String, Object>> handleBookingTooSoon(BookingTooSoonException ex) {
        Map<String, Object> response = buildResponseBody(HttpStatus.BAD_REQUEST, "BOOKING_TOO_SOON", ex.getMessage());
        response.put("minimumHours", ex.getMinimumHours());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BookingFullException.class)
    public ResponseEntity<Map<String, Object>> handleBookingFull(BookingFullException ex) {
        return buildResponse(HttpStatus.CONFLICT, "BOOKING_FULL", ex.getMessage());
    }

    @ExceptionHandler(InstructorNotAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleInstructorNotAvailable(InstructorNotAvailableException ex) {
        return buildResponse(HttpStatus.CONFLICT, "INSTRUCTOR_NOT_AVAILABLE", ex.getMessage());
    }

    @ExceptionHandler(InstructorNotAtSpotException.class)
    public ResponseEntity<Map<String, Object>> handleInstructorNotAtSpot(InstructorNotAtSpotException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INSTRUCTOR_NOT_AT_SPOT", ex.getMessage());
    }

    @ExceptionHandler(ParticipantAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleParticipantAlreadyExists(ParticipantAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "PARTICIPANT_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(CancellationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleCancellationNotAllowed(CancellationNotAllowedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "CANCELLATION_NOT_ALLOWED", ex.getMessage());
    }

    @ExceptionHandler(AvailabilityOverlapException.class)
    public ResponseEntity<Map<String, Object>> handleAvailabilityOverlap(AvailabilityOverlapException ex) {
        return buildResponse(HttpStatus.CONFLICT, "AVAILABILITY_OVERLAP", ex.getMessage());
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<Map<String, Object>> handleBookingException(BookingException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BOOKING_ERROR", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(buildResponseBody(status, code, message));
    }

    private Map<String, Object> buildResponseBody(HttpStatus status, String code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("code", code);
        response.put("message", message);
        return response;
    }
}