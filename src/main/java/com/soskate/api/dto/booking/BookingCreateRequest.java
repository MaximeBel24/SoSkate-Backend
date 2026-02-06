package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record BookingCreateRequest(

        @NotNull(message = "Le client est obligatoire")
        Long customerId,

        @NotNull(message = "L'instructeur est obligatoire")
        Long instructorId,

        @NotNull(message = "Le spot est obligatoire")
        Long spotId,

        @NotNull(message = "Le service est obligatoire")
        Long serviceId,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalDateTime startTime,

        @NotNull(message = "La durée est obligatoire")
        Integer durationMinutes,

        @NotNull(message = "Le nombre de participants est obligatoire")
        @Min(value = 1, message = "Il faut au moins 1 participant")
        Integer numberOfParticipants,

        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        String participantsNotes
) {}