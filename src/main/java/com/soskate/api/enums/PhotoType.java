package com.soskate.api.enums;

/**
 * Defines the purpose or category of a photo.
 * Allows different handling for avatars, covers, gallery images, etc.
 */
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

    public String getDescription() {
        return description;
    }

    /**
     * Maximum number of photos of this type allowed per entity.
     * 1 = unique (avatar, cover)
     * 999 = unlimited for practical purposes
     */
    public int getMaxPerEntity() {
        return maxPerEntity;
    }
}
