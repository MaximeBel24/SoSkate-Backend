package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Size;

public record ParticipantConfirmRequest(
        String paymentIntentId,

        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        String participantsNotes
) {}