package com.soskate.api.services.customer.auth;

import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.mappers.CustomerMapper;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.services.common.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the customer authentication service.
 * Handles registration with validation, password hashing, and database persistence.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new customer.
     *
     * @param customerToRegister the registration data
     * @return the created customer
     * @throws EmailAlreadyExistsException if the email already exists
     */
    @Override
    @Transactional
    public CustomerResponse registerCustomer(CustomerRegisterRequest customerToRegister) {
        log.info("Registration attempt for email: {}", customerToRegister.email());

        emailValidationService.validateEmailUnique(customerToRegister.email());

        String hashedPassword = passwordEncoder.encode(customerToRegister.password());
        log.debug("Password hashed successfully");

        CustomerEntity customer = customerMapper.toEntity(customerToRegister, hashedPassword);

        CustomerEntity savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully: ID {}, Email {}",
                savedCustomer.getId(),
                savedCustomer.getEmail());

        return customerMapper.toResponse(savedCustomer);
    }

    /**
     * Checks if an email already exists in the database.
     * Useful for frontend-side validation.
     *
     * @param email the email to check
     * @return true if the email already exists
     */
    @Override
    public boolean emailExists(String email) {
        log.debug("Checking email existence: {}", email);
        return customerRepository.existsByEmail(email);
    }
}
