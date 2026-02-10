package com.soskate.api.dto.booking;

import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.ParticipantStatus;

import java.time.LocalDateTime;

/**
 * DTO to display a user's bookings in "My Bookings".
 * Combines participation info + booking info.
 */
public record MyBookingResponse(
        // Participation info
        Long participationId,
        ParticipantStatus participantStatus,  // CONFIRMED, CANCELLED

        // Booking info
        Long bookingId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        BookingStatus bookingStatus,
        String participantsNotes,

        // Instructor info (summary)
        Long instructorId,
        String instructorFirstname,
        String instructorLastname,

        // Spot info (summary)
        Long spotId,
        String spotName,
        String spotAddress,
        String spotCity,

        // Service info (summary)
        Long serviceId,
        String serviceName,
        Integer basePriceCents,

        // Calculated price for this participation
        Integer totalPriceCents
) {

    /**
     * Checks whether the booking can be cancelled (> 48h before start).
     */
    public boolean canCancel() {
        if (participantStatus == ParticipantStatus.CANCELLED) {
            return false;
        }
        return startTime.isAfter(LocalDateTime.now().plusHours(48));
    }

    /**
     * Checks whether the notes can be edited (> 24h before start).
     */
    public boolean canEditNotes() {
        if (participantStatus == ParticipantStatus.CANCELLED ||
                bookingStatus == BookingStatus.COMPLETED) {
            return false;
        }
        return startTime.isAfter(LocalDateTime.now().plusHours(24));
    }

    /**
     * Checks whether the booking is in the past.
     */
    public boolean isPast() {
        return startTime.isBefore(LocalDateTime.now());
    }
}
