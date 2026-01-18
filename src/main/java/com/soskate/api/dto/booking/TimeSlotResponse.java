package com.soskate.api.dto.booking;

import java.time.LocalTime;

public record TimeSlotResponse(
        LocalTime startTime,
        LocalTime endTime,
        Boolean available
) {}