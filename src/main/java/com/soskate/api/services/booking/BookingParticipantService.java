package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.*;

import java.util.List;

/**
 * Service interface for booking participant operations.
 */
public interface BookingParticipantService {

    /**
     * Invites a participant to a booking (by instructor).
     */
    ParticipantResponse inviteByInstructor(Long bookingId, ParticipantInviteRequest request);

    /**
     * Invites a participant to a booking (by another participant).
     */
    ParticipantResponse inviteByParticipant(Long bookingId, Long inviterCustomerId, ParticipantInviteRequest request);

    /**
     * Accepts an invitation.
     */
    ParticipantResponse acceptInvitation(Long customerId, Long participantId);

    /**
     * Confirms participation after payment.
     */
    ParticipantResponse confirm(Long customerId, Long participantId, ParticipantConfirmRequest request);

    /**
     * Cancels a participation.
     */
    ParticipantResponse cancel(Long customerId, Long participantId, ParticipantCancelRequest request);

    /**
     * Gets all participants for a booking.
     */
    List<ParticipantResponse> getByBooking(Long bookingId);

    /**
     * Gets upcoming participations for a customer.
     */
    List<ParticipantResponse> getUpcomingByCustomer(Long customerId);

    /**
     * Gets pending invitations for a customer.
     */
    List<ParticipantResponse> getPendingInvitations(Long customerId);

    /**
     * Gets completed participations for a customer.
     */
    List<ParticipantResponse> getCompletedByCustomer(Long customerId);

    /**
     * Gets all bookings for a customer.
     */
    List<MyBookingResponse> getMyBookings(Long customerId);

    /**
     * Updates notes for a participation.
     */
    MyBookingResponse updateNotes(Long customerId, Long participationId, String notes);
}
