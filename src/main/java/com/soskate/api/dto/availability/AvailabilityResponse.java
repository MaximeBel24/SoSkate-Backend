package com.soskate.api.dto.availability;

import com.soskate.api.enums.AvailabilityStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AvailabilityResponse(
        Long id,
        Long instructorId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AvailabilityStatus status,
        LocalDateTime createdAt
) {}