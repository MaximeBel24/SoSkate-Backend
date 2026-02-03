package com.soskate.api.services.common;

/**
 * Service for centralized email validation across all user types.
 * Ensures email uniqueness across Customer and Instructor tables.
 */
public interface EmailValidationService {

    /**
     * Validates that the email is not already used by any user.
     * @param email the email to validate
     * @throws EmailAlreadyExistsException if email is already in use
     */
    void validateEmailUnique(String email);

    /**
     * Validates that the email is not used by another user (excluding the current one).
     * Used for profile updates where the user might keep their own email.
     * @param email the email to validate
     * @param currentUserId the ID of the user being updated
     * @param isCustomer true if the current user is a Customer, false if Instructor
     * @throws EmailAlreadyExistsException if email is used by another user
     */
    void validateEmailUniqueForUpdate(String email, Long currentUserId, boolean isCustomer);

    /**
     * Checks if an email exists in any user table.
     * @param email the email to check
     * @return true if the email exists
     */
    boolean emailExists(String email);

}
