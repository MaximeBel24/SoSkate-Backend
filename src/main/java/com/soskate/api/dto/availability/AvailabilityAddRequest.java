package com.soskate.api.dto.availability;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AvailabilityAddRequest(
        @NotEmpty(message = "At least one date is required")
        @Size(max = 100, message = "At most 100 dates at once")
        List<@NotNull(message = "A date cannot be empty") LocalDate> dates,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime
) {}
