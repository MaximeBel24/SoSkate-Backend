package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorCreateRequest;
import com.soskate.api.dto.instructor.InstructorResponse;

import java.util.List;

/**
 * Service interface for admin operations on instructors.
 */
public interface InstructorAdminService {

    /**
     * Creates a new instructor account and sends an invitation email.
     */
    InstructorResponse createInstructor(InstructorCreateRequest request);

    /**
     * Resends the invitation to an instructor with a new token.
     */
    InstructorResponse resendInvitation(Long instructorId);

    /**
     * Suspends an instructor account.
     */
    InstructorResponse suspendInstructor(Long instructorId);

    /**
     * Reactivates a suspended instructor account.
     */
    InstructorResponse reactivateInstructor(Long instructorId);

    /**
     * Deletes an instructor.
     */
    void deleteInstructorById(Long instructorId);

    /**
     * Gets instructors with pending invitations.
     */
    List<InstructorResponse> getPendingInvitations();

    /**
     * Gets instructors with expired invitations.
     */
    List<InstructorResponse> getExpiredInvitations();
}
