package com.soskate.api.enums;

import lombok.Getter;

/**
 * Represents the status of a participant within a booking.
 */
@Getter
public enum ParticipantStatus {

    /**
     * Participant has been invited but has not yet responded.
     */
    INVITED("Invité", "Invitation envoyée, en attente de réponse"),

    /**
     * Participant has accepted but payment is still pending.
     */
    PENDING_PAYMENT("En attente de paiement", "Inscription acceptée, paiement en cours"),

    /**
     * Participant is confirmed and payment has been received.
     */
    CONFIRMED("Confirmé", "Participation confirmée et payée"),

    /**
     * Participant has cancelled their registration.
     */
    CANCELLED("Annulé", "Participation annulée"),

    /**
     * Participant has been refunded after cancellation.
     */
    REFUNDED("Remboursé", "Participation annulée et remboursée"),

    /**
     * Participant has completed the session.
     */
    COMPLETED("Terminé", "Session terminée avec succès"),

    /**
     * Participant did not show up for the session.
     */
    NO_SHOW("Absent", "Non-présentation à la session");

    private final String displayName;
    private final String description;

    ParticipantStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
