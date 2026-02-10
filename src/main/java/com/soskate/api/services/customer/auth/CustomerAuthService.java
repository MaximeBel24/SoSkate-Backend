package com.soskate.api.services.customer.auth;

import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;

/**
 * Service dedicated to customer authentication.
 * Handles registration and email verification.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface CustomerAuthService {

    /**
     * Registers a new customer in the system.
     * Validates email uniqueness, hashes the password, and saves to the database.
     *
     * @param customerRegisterDto the registration data
     * @return the created customer (without the password)
     * @throws EmailAlreadyExistsException if the email already exists
     */
    CustomerResponse registerCustomer(CustomerRegisterRequest customerRegisterDto);

    /**
     * Checks if an email already exists in the database.
     * Useful for frontend-side validation before form submission.
     *
     * @param email the email to check
     * @return true if the email already exists
     */
    boolean emailExists(String email);
}
