package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParticipantInviteRequest(
        @NotNull(message = "Le client est obligatoire")
        Long customerId,

        @Min(value = 1, message = "Il faut au moins 1 participant")
        Integer numberOfParticipants,

        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        String participantsNotes
) {}