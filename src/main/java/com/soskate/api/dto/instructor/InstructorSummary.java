package com.soskate.api.dto.instructor;

import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.SkateSpecialty;

/**
 * Lightweight response record for instructor data in list views.
 * Contains only essential information for displaying instructor cards.
 */
public record InstructorSummary(
        Long id,
        String firstname,
        String lastname,
        String phone,
        InstructorStatus status,
        SkateSpecialty specialty,
        Integer yearsOfExperience
) {

    /**
     * Returns the full name of the instructor.
     */
    public String fullName() {
        return firstname + " " + lastname;
    }
}