package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorSpotRequest;
import com.soskate.api.dto.instructor.InstructorSpotResponse;
import com.soskate.api.dto.instructor.InstructorSummary;

import java.util.List;

/**
 * Service interface for instructor-spot associations.
 */
public interface InstructorSpotService {

    /**
     * Adds a spot to an instructor's teaching locations.
     */
    InstructorSpotResponse addSpotToInstructor(Long instructorId, InstructorSpotRequest request);

    /**
     * Gets all spots where an instructor teaches.
     */
    List<InstructorSpotResponse> getSpotsByInstructor(Long instructorId);

    /**
     * Gets all active instructors at a specific spot.
     */
    List<InstructorSummary> getInstructorsBySpot(Long spotId);

    /**
     * Removes a spot from an instructor's teaching locations.
     */
    void removeSpotFromInstructor(Long instructorId, Long spotId);

    /**
     * Checks if an instructor teaches at a specific spot.
     */
    boolean instructorTeachesAtSpot(Long instructorId, Long spotId);
}
