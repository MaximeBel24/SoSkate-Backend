package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Size;

public record ParticipantCancelRequest(
        @Size(max = 500, message = "La raison ne peut pas dépasser 500 caractères")
        String reason
) {}