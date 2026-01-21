package com.soskate.api.dto.booking;

import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.ParticipantStatus;

import java.time.LocalDateTime;

/**
 * DTO pour afficher les réservations d'un utilisateur dans "Mes réservations"
 * Combine les infos de la participation + du booking
 */
public record MyBookingResponse(
        // Infos participation
        Long participationId,
        ParticipantStatus participantStatus,  // CONFIRMED, CANCELLED

        // Infos booking
        Long bookingId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        BookingStatus bookingStatus,
        String participantsNotes,

        // Infos instructeur (résumé)
        Long instructorId,
        String instructorFirstname,
        String instructorLastname,

        // Infos spot (résumé)
        Long spotId,
        String spotName,
        String spotAddress,
        String spotCity,

        // Infos service (résumé)
        Long serviceId,
        String serviceName,
        Integer basePriceCents,

        // Prix calculé pour cette participation
        Integer totalPriceCents
) {

    /**
     * Vérifie si la réservation peut être annulée (> 48h avant)
     */
    public boolean canCancel() {
        if (participantStatus == ParticipantStatus.CANCELLED) {
            return false;
        }
        return startTime.isAfter(LocalDateTime.now().plusHours(48));
    }

    /**
     * Vérifie si les notes peuvent être modifiées (> 24h avant)
     */
    public boolean canEditNotes() {
        if (participantStatus == ParticipantStatus.CANCELLED ||
                bookingStatus == BookingStatus.COMPLETED) {
            return false;
        }
        return startTime.isAfter(LocalDateTime.now().plusHours(24));
    }

    /**
     * Vérifie si la réservation est passée
     */
    public boolean isPast() {
        return startTime.isBefore(LocalDateTime.now());
    }
}