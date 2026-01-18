package com.soskate.api.dto.instructor;

import jakarta.validation.constraints.NotNull;

public record InstructorSpotRequest(
        @NotNull(message = "Le spot est obligatoire")
        Long spotId
) {}