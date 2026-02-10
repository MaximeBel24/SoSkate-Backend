package com.soskate.api.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Unified response DTO after successful login.
 * Supports both Customers and Instructors.
 *
 * The `role` field allows the frontend to determine the user type
 * and adapt the interface accordingly.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(

        // === Identifiers ===
        Long id,                    // User entity ID (parent)
        Long customerId,            // Specific ID if Customer (null otherwise)
        Long instructorId,          // Specific ID if Instructor (null otherwise)

        // === User info ===
        String email,
        String firstName,
        String lastName,
        String phone,
        String token,

        // === Role ===
        UserRole role,

        // === Message ===
        String message
) {

    /**
     * Enum to identify the user type.
     */
    public enum UserRole {
        CUSTOMER,
        INSTRUCTOR
    }

    /**
     * Factory method to create a Customer response.
     */
    public static LoginResponse forCustomer(
            Long userId,
            Long customerId,
            String email,
            String firstName,
            String lastName,
            String phone,
            String token
    ) {
        return new LoginResponse(
                userId,
                customerId,
                null,
                email,
                firstName,
                lastName,
                phone,
                token,
                UserRole.CUSTOMER,
                "Login successful"
        );
    }

    /**
     * Factory method to create an Instructor response.
     */
    public static LoginResponse forInstructor(
            Long userId,
            Long instructorId,
            String email,
            String firstName,
            String lastName,
            String phone,
            String token
    ) {
        return new LoginResponse(
                userId,
                null,  // customerId
                instructorId,
                email,
                firstName,
                lastName,
                phone,
                token,
                UserRole.INSTRUCTOR,
                "Login successful"
        );
    }
}
