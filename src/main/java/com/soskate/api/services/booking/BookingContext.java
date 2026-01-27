package com.soskate.api.services.booking;

import com.soskate.api.entities.*;

/**
 * Encapsulates all entities needed to create or validate a booking.
 * Loaded by BookingContextLoader, used by BookingService and BookingValidationService.
 */
public record BookingContext(
        CustomerEntity customer,
        InstructorEntity instructor,
        SpotEntity spot,
        ServiceEntity service,
        PlatformSettingsEntity settings
) {
    /**
     * Calculates the price per participant based on service base price and duration.
     */
    public int calculatePricePerParticipant(int durationMinutes) {
        double hours = durationMinutes / 60.0;
        return (int) Math.round(service.getBasePriceCents() * hours);
    }

    /**
     * Calculates the total amount for a given number of participants.
     */
    public int calculateTotalAmount(int durationMinutes, int numberOfParticipants) {
        return calculatePricePerParticipant(durationMinutes) * numberOfParticipants;
    }
}
