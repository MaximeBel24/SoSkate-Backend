package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.LoginResponse;
import com.soskate.api.exceptions.auth.BadCredentialsException;

/**
 * Unified authentication service.
 * Handles login for all user types (Customer and Instructor).
 *
 * This service automatically detects the user type by searching
 * first in Customers, then in Instructors.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface AuthService {

    /**
     * Authenticates a user (Customer or Instructor) with their email and password.
     *
     * The method searches first in Customers, then in Instructors.
     * The returned role allows the frontend to adapt the interface.
     *
     * @param loginRequest email + password
     * @return the unified login information with the role
     * @throws BadCredentialsException if email or password is invalid
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Checks if an email already exists in the system (Customer OR Instructor).
     *
     * @param email the email to check
     * @return true if the email already exists
     */
    boolean emailExists(String email);
}