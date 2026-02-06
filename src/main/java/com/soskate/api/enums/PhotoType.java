package com.soskate.api.enums;

import lombok.Getter;

/**
 * Defines the purpose or category of a photo.
 * Allows different handling for avatars, covers, gallery images, etc.
 */
@Getter
public enum PhotoType {
    AVATAR("Profile picture - unique per user", 1),
    COVER("Cover photo - main visual for events", 1),
    GALLERY("Gallery photo - collection of images", 999),
    TRICK("Trick/Course photo - instructor portfolio", 50);

    private final String description;
    private final int maxPerEntity;

    PhotoType(String description, int maxPerEntity) {
        this.description = description;
        this.maxPerEntity = maxPerEntity;
    }

}
