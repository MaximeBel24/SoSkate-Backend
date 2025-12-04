package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.auth.BadCredentialsException;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.exceptions.auth.InvalidActivationTokenException;
import com.soskate.api.exceptions.auth.PasswordMismatchException;
import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.common.ValidationErrorResponse;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.photo.InvalidPhotoFormatException;
import com.soskate.api.exceptions.photo.PhotoNotFoundException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.exceptions.service.ServiceAlreadyExistsException;
import com.soskate.api.exceptions.service.ServiceNotActiveException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import com.soskate.api.exceptions.spot.DuplicateSpotException;
import com.soskate.api.exceptions.spot.InvalidCoordinatesException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
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
 * Gestionnaire global des exceptions pour l'API REST.
 * Intercepte les exceptions et retourne des réponses JSON structurées.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // ========================================
    // EXCEPTIONS D'AUTHENTIFICATION
    // ========================================

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.error("EmailAlreadyExistsException interceptée : Un compte avec l'email '{}' existe déjà", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gère l'exception BadCredentialsException.
     * Retourne un statut 401 UNAUTHORIZED.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.error("BadCredentialsException interceptée : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ========================================
    // EXCEPTIONS SERVICE
    // ========================================

    /**
     * Gère l'exception ServiceNotFoundException.
     * Retourne un statut 404 NOT FOUND.
     */
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotFound(ServiceNotFoundException ex) {
        log.error("ServiceNotFoundException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gère l'exception ServiceAlreadyExistsException.
     * Retourne un statut 409 CONFLICT.
     */
    @ExceptionHandler(ServiceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleServiceAlreadyExists(ServiceAlreadyExistsException ex) {
        log.error("ServiceAlreadyExistsException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gère l'exception ServiceNotActiveException.
     * Retourne un statut 400 BAD REQUEST.
     */
    @ExceptionHandler(ServiceNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotActive(ServiceNotActiveException ex) {
        log.error("ServiceNotActiveException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================
    // EXCEPTIONS SPOT
    // ========================================

    /**
     * Gère l'exception SpotNotFoundException.
     * Retourne un statut 404 NOT FOUND.
     */
    @ExceptionHandler(SpotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSpotNotFound(SpotNotFoundException ex) {
        log.error("SpotNotFoundException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gère l'exception DuplicateSpotException.
     * Retourne un statut 409 CONFLICT.
     */
    @ExceptionHandler(DuplicateSpotException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSpot(DuplicateSpotException ex) {
        log.error("DuplicateSpotException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Gère l'exception InvalidCoordinatesException.
     * Retourne un statut 400 BAD REQUEST.
     */
    @ExceptionHandler(InvalidCoordinatesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCoordinates(InvalidCoordinatesException ex) {
        log.error("InvalidCoordinatesException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================
    // EXCEPTIONS PHOTO
    // ========================================

    /**
     * Gère l'exception PhotoNotFoundException.
     * Retourne un statut 404 NOT FOUND.
     */
    @ExceptionHandler(PhotoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhotoNotFound(PhotoNotFoundException ex) {
        log.error("PhotoNotFoundException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Gère l'exception PhotoUploadException.
     * Retourne un statut 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(PhotoUploadException.class)
    public ResponseEntity<ErrorResponse> handlePhotoUpload(PhotoUploadException ex) {
        log.error("PhotoUploadException : {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Gère l'exception InvalidPhotoFormatException.
     * Retourne un statut 400 BAD REQUEST.
     */
    @ExceptionHandler(InvalidPhotoFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPhotoFormat(InvalidPhotoFormatException ex) {
        log.error("InvalidPhotoFormatException : {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ========================================
    // VALIDATION ET FORMAT
    // ========================================

    /**
     * Gère les erreurs de validation Bean Validation (@NotBlank, @Email, @Pattern, etc.).
     * Retourne un statut 400 BAD REQUEST avec les détails de chaque erreur.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error("MethodArgumentNotValidException interceptée !");
        log.error("Nombre d'erreurs : {}", ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            log.error("Champ '{}' : {}", fieldName, errorMessage);
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erreur(s) de validation des données",
                LocalDateTime.now(),
                errors
        );

        log.info("Réponse envoyée : {}", response);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Gère les erreurs de parsing JSON (body mal formé, types incompatibles, etc.).
     * Retourne un statut 400 BAD REQUEST.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        System.out.println("========================================");
        System.out.println("??? HTTP MESSAGE NOT READABLE ???");
        System.out.println("========================================");

        log.error("HttpMessageNotReadableException interceptée");

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

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    // ========================================
    // EXCEPTIONS PHOTO
    // ========================================

    @ExceptionHandler(InstructorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInstructorNotFound(InstructorNotFoundException ex) {
        log.warn("Instructor not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidActivationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidActivationToken(InvalidActivationTokenException ex) {
        log.warn("Invalid activation token: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {
        log.warn("Password mismatch: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ========================================
    // EXCEPTION GÉNÉRIQUE
    // ========================================

    /**
     * Gère toutes les autres exceptions non prévues.
     * Retourne un statut 500 INTERNAL SERVER ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Exception générique interceptée : {}", ex.getClass().getSimpleName());
        log.error("Message : {}", ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Une erreur interne est survenue",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}