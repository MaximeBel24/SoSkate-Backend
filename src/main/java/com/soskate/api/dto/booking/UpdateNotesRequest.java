package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Size;

public record UpdateNotesRequest(
        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes
) {}
