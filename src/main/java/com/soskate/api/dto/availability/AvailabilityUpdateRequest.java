package com.soskate.api.dto.availability;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityUpdateRequest(
        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Current start time is required")
        LocalTime currentStartTime,

        @NotNull(message = "Current end time is required")
        LocalTime currentEndTime,

        @NotNull(message = "New start time is required")
        LocalTime newStartTime,

        @NotNull(message = "New end time is required")
        LocalTime newEndTime,

        boolean confirmed
) {}
