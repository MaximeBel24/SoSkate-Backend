package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Size;

public record UpdateNotesRequest(
        @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
        String notes
) {}