package com.soskate.api.exceptions.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Classe de réponse standardisée pour les erreurs de l'API
 *
 * Structure JSON retournée :
 * {
 *   "code": "SERVICE_NOT_FOUND",
 *   "message": "Service with id 123 not found",
 *   "path": "/services/123",
 *   "timestamp": "2024-09-18T14:30:00",
 *   "details": {...}  // optionnel
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // N'inclut que les champs non-null dans le JSON
@Schema(description = "Structure de réponse d'erreur standardisée")
public record ErrorResponse(

        @Schema(description = "Code d'erreur unique", example = "SERVICE_NOT_FOUND")
        String code,

        @Schema(description = "Message d'erreur lisible", example = "Service with id 123 not found")
        String message,

        @Schema(description = "Chemin de l'endpoint qui a généré l'erreur", example = "/services/123")
        String path,

        @Schema(description = "Horodatage de l'erreur", example = "2024-09-18T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "Détails additionnels sur l'erreur (optionnel)")
        Map<String, Object> details
) {

    /**
     * Constructeur simple sans détails
     */
    public ErrorResponse(String code, String message, String path) {
        this(code, message, path, LocalDateTime.now(), null);
    }

    /**
     * Constructeur avec détails
     */
    public ErrorResponse(String code, String message, String path, Map<String, Object> details) {
        this(code, message, path, LocalDateTime.now(), details);
    }

    /**
     * Factory method pour les erreurs de validation
     */
    public static ErrorResponse validationError(String path, List<FieldError> fieldErrors) {
        Map<String, Object> details = Map.of(
                "fieldErrors", fieldErrors,
                "errorCount", fieldErrors.size()
        );

        String message = fieldErrors.size() == 1
                ? "1 erreur de validation"
                : fieldErrors.size() + " erreurs de validation";

        return new ErrorResponse("VALIDATION_ERROR", message, path, details);
    }

    /**
     * Factory method pour les erreurs techniques
     */
    public static ErrorResponse technicalError(String path) {
        return new ErrorResponse(
                "TECHNICAL_ERROR",
                "Une erreur technique est survenue. Veuillez réessayer plus tard.",
                path
        );
    }

    /**
     * Factory method pour les erreurs de ressource non trouvée
     */
    public static ErrorResponse notFound(String resource, Object id, String path) {
        return new ErrorResponse(
                resource.toUpperCase() + "_NOT_FOUND",
                String.format("%s with id %s not found", resource, id),
                path
        );
    }

    /**
     * Factory method pour les erreurs de conflit
     */
    public static ErrorResponse conflict(String resource, String conflictReason, String path) {
        return new ErrorResponse(
                resource.toUpperCase() + "_ALREADY_EXISTS",
                conflictReason,
                path
        );
    }

    /**
     * Record interne pour les erreurs de validation de champs
     */
    public record FieldError(
            @Schema(description = "Nom du champ en erreur", example = "name")
            String field,

            @Schema(description = "Valeur rejetée", example = "")
            Object rejectedValue,

            @Schema(description = "Message d'erreur", example = "ne doit pas être vide")
            String message
    ) {}
}