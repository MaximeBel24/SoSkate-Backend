package com.soskate.api.enums;

import lombok.Getter;

/**
 * Represents the lifecycle status of a booking (lesson, event, session).
 */
@Getter
public enum BookingStatus {

    /**
     * Booking is open and accepting participants.
     */
    OPEN("Ouvert", "La réservation est ouverte aux inscriptions"),

    /**
     * Booking has reached maximum capacity.
     */
    FULL("Complet", "La réservation a atteint sa capacité maximale"),

    /**
     * Booking is confirmed and will take place as scheduled.
     */
    CONFIRMED("Confirmé", "La réservation est confirmée"),

    /**
     * Booking has been completed successfully.
     */
    COMPLETED("Terminé", "La session s'est déroulée avec succès"),

    /**
     * Booking has been cancelled.
     */
    CANCELLED("Annulé", "La réservation a été annulée");

    private final String displayName;
    private final String description;

    BookingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
