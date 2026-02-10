package com.soskate.api.dto.availability;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an actually available time slot (after subtracting bookings).
 * Used for displaying the instructor schedule and client-side booking.
 */
public record AvailableSlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {}
