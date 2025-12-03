package com.soskate.api.dto.instructor;

import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.SkateSpecialty;

import java.time.LocalDateTime;

/**
 * Response record for instructor data returned in API responses.
 * Contains all public information about an instructor.
 */
public record InstructorResponse(
        Long id,
        String email,
        String firstname,
        String lastname,
        String phone,
        InstructorStatus status,
        String bio,
        SkateSpecialty specialty,
        Integer yearsOfExperience,
        String instagramHandle,
        String youtubeChannel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime invitedAt,
        LocalDateTime activatedAt
) {

    /**
     * Builds the full Instagram profile URL from the handle.
     */
    public String instagramUrl() {
        if (instagramHandle == null || instagramHandle.isBlank()) {
            return null;
        }
        return "https://instagram.com/" + instagramHandle;
    }

    /**
     * Builds the full YouTube channel URL from the handle.
     */
    public String youtubeUrl() {
        if (youtubeChannel == null || youtubeChannel.isBlank()) {
            return null;
        }
        // Handle both @handle format and channel ID format
        if (youtubeChannel.startsWith("UC")) {
            return "https://youtube.com/channel/" + youtubeChannel;
        }
        return "https://youtube.com/@" + youtubeChannel;
    }

    /**
     * Returns the full name of the instructor.
     */
    public String fullName() {
        return firstname + " " + lastname;
    }
}