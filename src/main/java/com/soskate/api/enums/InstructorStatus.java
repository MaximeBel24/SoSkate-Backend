package com.soskate.api.enums;

import lombok.Getter;

/**
 * Represents the different states of an instructor account lifecycle.
 */
@Getter
public enum InstructorStatus {

    /**
     * Instructor has been invited by an admin but has not yet activated their account.
     * The invitation token is valid and the instructor can complete their registration.
     */
    INVITED("Invité", "En attente d'activation du compte"),

    /**
     * Instructor has activated their account and can fully use the platform.
     * They can manage their schedule, receive bookings, and update their profile.
     */
    ACTIVE("Actif", "Compte actif et opérationnel"),

    /**
     * Instructor account has been temporarily suspended by an admin.
     * They cannot receive new bookings but their data is preserved.
     */
    SUSPENDED("Suspendu", "Compte temporairement suspendu");

    private final String displayName;
    private final String description;

    InstructorStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}