package com.soskate.api.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO pour les requêtes de création et mise à jour d'événements
 */
@Schema(description = "Données requises pour créer ou mettre à jour un événement")
public record EventRequestDto(

        @Schema(description = "Titre de l'événement", example = "Compétition Skateboard Débutants")
        @NotBlank(message = "Title is mandatory")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @Schema(description = "Description détaillée de l'événement", example = "Compétition ouverte aux skateurs débutants avec prix à gagner")
        @NotBlank(message = "Description is mandatory")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @Schema(description = "Date et heure de début de l'événement", example = "2024-12-25T10:00:00")
        @NotNull(message = "Start time is mandatory")
        @Future(message = "Start time must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @Schema(description = "Date et heure de fin de l'événement", example = "2024-12-25T18:00:00")
        @NotNull(message = "End time is mandatory")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,

        @Schema(description = "Nombre maximum de participants", example = "20")
        @NotNull(message = "Maximum participants is mandatory")
        @Min(value = 1, message = "Maximum participants must be at least 1")
        @Max(value = 1000, message = "Maximum participants cannot exceed 1000")
        Integer maxParticipants,

        @Schema(description = "ID du spot où se déroule l'événement", example = "1")
        @NotNull(message = "Spot ID is mandatory")
        @Positive(message = "Spot ID must be positive")
        Long spotId,

        @Schema(description = "Prix d'inscription en centimes d'euro (optionnel)", example = "1500")
        @Min(value = 0, message = "Registration price cannot be negative")
        Long registrationPriceCents,

        @Schema(description = "Indique si l'événement est actif", example = "true")
        @NotNull(message = "Active status is mandatory")
        Boolean isActive

) {
    
}