package com.soskate.api.services.availability;

import com.soskate.api.dto.booking.AvailableSlotsResponse;

import java.time.LocalDate;

/**
 * Service interface for calculating available booking slots.
 */
public interface SlotCalculationService {

    /**
     * Calculates available time slots for an instructor at a specific spot.
     *
     * @param instructorId    The instructor ID
     * @param spotId          The spot ID
     * @param date            The date to check
     * @param durationMinutes The requested booking duration
     * @return Available slots with their availability status
     */
    AvailableSlotsResponse calculateAvailableSlots(
            Long instructorId,
            Long spotId,
            LocalDate date,
            Integer durationMinutes
    );
}
