package com.soskate.api.enums;

import lombok.Getter;

@Getter
public enum ServiceType {
    LESSON("Cours", "Cours de skateboard"),
    RENTAL("Location", "Location de matériel"),
    SUBSCRIPTION("Abonnement", "Abonnement à l'offre partenaire"),
    EVENT("Événement", "Événement ou concours de skate"),
    PRIVATE_COACHING("Coaching privé", "Coaching privé avec l'instructeur de votre choix");

    private final String displayName;
    private final String description;

    ServiceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
