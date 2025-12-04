package com.soskate.api.enums;

import lombok.Getter;

/**
 * Represents the different skateboarding disciplines an instructor can specialize in.
 */
@Getter
public enum SkateSpecialty {

    STREET("Street", "Skateboard urbain avec tricks sur mobilier urbain (rails, escaliers, bancs)"),
    BOWL("Bowl", "Skateboard en bowl et piscines, transitions et aerials"),
    VERT("Vert", "Skateboard sur rampe verticale (half-pipe)"),
    PARK("Park", "Skateboard en skatepark, mix de street et transitions"),
    LONGBOARD("Longboard", "Longboard pour cruising, downhill ou dancing"),
    FREESTYLE("Freestyle", "Tricks techniques au sol (flatground)"),
    CRUISING("Cruising", "Balade et déplacement urbain en skateboard");

    private final String displayName;
    private final String description;

    SkateSpecialty(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}