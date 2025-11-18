package com.soskate.api.enums;

import lombok.Getter;

/**
 * Defines the type of entity a photo is associated with.
 * Used for polymorphic relationships in PhotoEntity.
 */
@Getter
public enum PhotoEntityType {
    SPOT("Spot - Skatepark or street spot"),
    CUSTOMER("Customer - User profile"),
    INSTRUCTOR("Instructor - Teacher profile"),
    EVENT("Event - Skateboarding event or session");

    private final String description;

    PhotoEntityType(String description) {
        this.description = description;
    }

}
