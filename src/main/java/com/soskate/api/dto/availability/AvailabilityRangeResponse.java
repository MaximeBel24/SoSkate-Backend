package com.soskate.api.dto.availability;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityRangeResponse(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {}
