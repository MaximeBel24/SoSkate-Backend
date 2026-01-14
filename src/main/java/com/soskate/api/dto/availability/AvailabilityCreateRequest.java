package com.soskate.api.dto.availability;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityCreateRequest(
        @NotNull(message = "La date est obligatoire")
        LocalDate date,

        @NotNull(message = "L'heure de début est obligatoire")
        LocalTime startTime,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalTime endTime
) {}