package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.common.PlatformSettingsNotFoundException;
import com.soskate.api.exceptions.common.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for common/generic exceptions.
 * Domain-specific exceptions are handled by their respective handlers.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(PlatformSettingsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlatformSettingsNotFound(PlatformSettingsNotFoundException ex) {
        log.warn("Platform settings not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "PLATFORM_SETTINGS_NOT_FOUND", ex.getMessage()));
    }

    /**
     * Handles Bean Validation errors (@NotBlank, @Email, @Pattern, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("Validation error: {} field(s) invalid", ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erreur(s) de validation des données",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles JSON parsing errors (malformed body, type mismatches, etc.).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        log.warn("HTTP message not readable: {}", ex.getMessage());

        String message = "Le format de la requête est invalide. Vérifiez le JSON envoyé.";

        if (ex.getCause() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage != null) {
                if (causeMessage.contains("LocalDate")) {
                    message = "Format de date invalide. Utilisez le format : yyyy-MM-dd (ex: 1995-06-15)";
                } else if (causeMessage.contains("JSON")) {
                    message = "JSON invalide. Vérifiez la syntaxe (virgules, guillemets, accolades)";
                }
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST", message));
    }

    /**
     * Handles IllegalStateException (business rule violations).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "ILLEGAL_STATE", ex.getMessage()));
    }

    /**
     * Handles IllegalArgumentException (invalid input).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "INVALID_ARGUMENT", ex.getMessage()));
    }

    /**
     * Fallback handler for all unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "INTERNAL_ERROR",
                        "Une erreur interne est survenue"
                ));
    }
}
