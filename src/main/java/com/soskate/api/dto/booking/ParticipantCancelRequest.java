package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Size;

public record ParticipantCancelRequest(
        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
) {}
