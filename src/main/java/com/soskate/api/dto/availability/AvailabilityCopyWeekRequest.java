package com.soskate.api.dto.availability;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AvailabilityCopyWeekRequest(
        @NotNull(message = "The Monday of the week is required")
        LocalDate weekStart,

        @NotNull(message = "The number of weeks is required")
        @Min(value = 1, message = "At least 1 week")
        @Max(value = 12, message = "At most 12 weeks")
        Integer weeks
) {}
