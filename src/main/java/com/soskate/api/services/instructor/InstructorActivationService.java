package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorActivateRequest;
import com.soskate.api.dto.instructor.InstructorResponse;

/**
 * Service interface for instructor account activation.
 */
public interface InstructorActivationService {

    /**
     * Activates an instructor account using the activation token.
     */
    InstructorResponse activateAccount(InstructorActivateRequest request);

    /**
     * Validates an activation token without activating.
     */
    boolean validateActivationToken(String token);
}
