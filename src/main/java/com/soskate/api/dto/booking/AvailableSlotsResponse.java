package com.soskate.api.dto.booking;

import java.time.LocalDate;
import java.util.List;

public record AvailableSlotsResponse(
        Long instructorId,
        Long spotId,
        LocalDate date,
        Integer durationMinutes,
        List<TimeSlotResponse> slots
) {}