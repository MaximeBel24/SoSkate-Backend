package com.soskate.api.services.availability;

import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.SpotEntity;

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

    /**
     * What can be booked with an instructor on one day, for one spot.
     * Closed when the instructor is inactive or does not teach there, or when the date is outside the open period.
     *
     * @param instructor The instructor
     * @param spot       The requested spot
     * @param date       The date to check
     * @return The bookable day, used both to list the slots and to validate a booking
     */
    BookableDay bookableDay(InstructorEntity instructor, SpotEntity spot, LocalDate date);
}
