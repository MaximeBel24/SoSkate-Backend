package com.soskate.api.dto.availability;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityUpdateRequest(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {}