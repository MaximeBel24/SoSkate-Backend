package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.spot.DuplicateSpotException;
import com.soskate.api.exceptions.spot.InvalidCoordinatesException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for spot-related exceptions.
 */
@Slf4j
@RestControllerAdvice
public class SpotExceptionHandler {

    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSpotNotFound(SpotNotFoundException ex) {
        log.warn("Spot not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "SPOT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateSpotException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSpot(DuplicateSpotException ex) {
        log.warn("Duplicate spot: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), "SPOT_DUPLICATE", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCoordinates(InvalidCoordinatesException ex) {
        log.warn("Invalid coordinates: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "INVALID_COORDINATES", ex.getMessage()));
    }
}
