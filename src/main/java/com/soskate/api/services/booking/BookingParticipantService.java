package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.*;

import java.util.List;

/**
 * Service interface for booking participant operations.
 */
public interface BookingParticipantService {

    /**
     * Cancels a participation.
     */
    ParticipantResponse cancel(Long customerId, Long participantId, ParticipantCancelRequest request);

    /**
     * Gets all bookings for a customer.
     */
    List<MyBookingResponse> getMyBookings(Long customerId);

    /**
     * Updates notes for a participation.
     */
    MyBookingResponse updateNotes(Long customerId, Long participationId, String notes);
}
