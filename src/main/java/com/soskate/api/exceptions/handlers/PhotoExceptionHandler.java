package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.photo.InvalidPhotoFormatException;
import com.soskate.api.exceptions.photo.PhotoNotFoundException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for photo-related exceptions.
 */
@Slf4j
@RestControllerAdvice
public class PhotoExceptionHandler {

    @ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhotoNotFound(PhotoNotFoundException ex) {
        log.warn("Photo not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "PHOTO_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(PhotoUploadException.class)
    public ResponseEntity<ErrorResponse> handlePhotoUpload(PhotoUploadException ex) {
        log.error("Photo upload error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PHOTO_UPLOAD_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPhotoFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPhotoFormat(InvalidPhotoFormatException ex) {
        log.warn("Invalid photo format: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "INVALID_PHOTO_FORMAT", ex.getMessage()));
    }
}
