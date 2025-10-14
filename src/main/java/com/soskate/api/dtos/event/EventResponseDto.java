package com.soskate.api.dtos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les événements
 * Contient toutes les informations d'un événement pour les réponses API
 */
@Schema(description = "Informations complètes d'un événement")
public record EventResponseDto(

        @Schema(description = "Identifiant unique de l'événement", example = "1")
        Long id,

        @Schema(description = "Titre de l'événement", example = "Compétition Skateboard Débutants")
        String title,

        @Schema(description = "Description détaillée de l'événement", example = "Compétition ouverte aux skateurs débutants avec prix à gagner")
        String description,

        @Schema(description = "Date et heure de début de l'événement", example = "2024-12-25T10:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime,

        @Schema(description = "Date et heure de fin de l'événement", example = "2024-12-25T18:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime,

        @Schema(description = "Nombre maximum de participants", example = "20")
        Integer maxParticipants,

//        @Schema(description = "ID du spot où se déroule l'événement", example = "1")
//        Long spotId,
//
//        @Schema(description = "Prix d'inscription en centimes d'euro", example = "1500")
//        Long registrationPriceCents,

        @Schema(description = "Indique si l'événement est actif", example = "true")
        Boolean isActive,

        @Schema(description = "Date de création de l'événement", example = "2024-09-18T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Date de dernière modification", example = "2024-09-18T14:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt

) {

}