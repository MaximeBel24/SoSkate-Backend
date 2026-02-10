package com.soskate.api.services.customer;

import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.dto.customer.CustomerUpdateRequest;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.mappers.CustomerMapper;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.services.common.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Customer profile management service.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailValidationService emailValidationService;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
        log.info("Updating profile for customer ID: {}", customerId);

        // 1. Retrieve the existing customer
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found with ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found with id: " + customerId);
                });

        // 2. Check email uniqueness if modified
        if (request.email() != null && !request.email().equalsIgnoreCase(customer.getEmail())) {
            emailValidationService.validateEmailUniqueForUpdate(request.email(), customerId, true);
        }

        // 3. Partial update (only non-null fields)
        if (request.firstname() != null) {
            customer.setFirstName(request.firstname());
            log.debug("First name updated: {}", request.firstname());
        }

        if (request.lastname() != null) {
            customer.setLastName(request.lastname());
            log.debug("Last name updated: {}", request.lastname());
        }

        if (request.email() != null) {
            customer.setEmail(request.email());
            log.debug("Email updated: {}", request.email());
        }

        if (request.phone() != null) {
            customer.setPhone(request.phone());
            log.debug("Phone updated: {}", request.phone());
        }

        if (request.birthDate() != null) {
            customer.setBirthDate(request.birthDate());
            log.debug("Birth date updated: {}", request.birthDate());
        }

        // 4. Save and return
        CustomerEntity updatedCustomer = customerRepository.save(customer);
        log.info("Profile updated successfully for customer ID: {}", customerId);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long customerId) {
        log.debug("Retrieving profile for customer ID: {}", customerId);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found with ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found with id: " + customerId);
                });

        return customerMapper.toResponse(customer);
    }
}