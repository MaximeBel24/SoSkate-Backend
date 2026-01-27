package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.dto.instructor.InstructorUpdateRequest;
import com.soskate.api.enums.SkateSpecialty;

import java.util.List;

/**
 * Service interface for instructor read operations and profile updates.
 */
public interface InstructorQueryService {

    /**
     * Gets an instructor by ID.
     */
    InstructorResponse getInstructorById(Long instructorId);

    /**
     * Gets all instructors (for admin).
     */
    List<InstructorResponse> getAllInstructors();

    /**
     * Gets all active instructors (for public listing).
     */
    List<InstructorSummary> getActiveInstructors();

    /**
     * Gets instructors by specialty.
     */
    List<InstructorSummary> getInstructorsBySpecialty(SkateSpecialty specialty);

    /**
     * Searches instructors by name.
     */
    List<InstructorSummary> searchInstructors(String query);

    /**
     * Updates an instructor's profile.
     */
    InstructorResponse updateProfile(Long instructorId, InstructorUpdateRequest request);
}
