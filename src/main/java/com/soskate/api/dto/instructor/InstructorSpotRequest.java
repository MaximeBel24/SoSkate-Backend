package com.soskate.api.dto.instructor;

import jakarta.validation.constraints.NotNull;

public record InstructorSpotRequest(
        @NotNull(message = "Spot is required")
        Long spotId
) {}
