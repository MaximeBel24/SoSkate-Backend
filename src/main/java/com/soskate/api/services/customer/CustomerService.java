package com.soskate.api.services.customer;

import com.soskate.api.dto.customer.CustomerUpdateRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;

/**
 * Service for Customer profile management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface CustomerService {

    /**
     * Updates a customer's profile.
     * Only non-null fields are updated (partial update).
     *
     * @param customerId the ID of the customer to update
     * @param request the data to update
     * @return the updated customer
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws EmailAlreadyExistsException if the new email is already in use
     */
    CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request);

    /**
     * Retrieves a customer's profile by their ID.
     *
     * @param customerId the customer's ID
     * @return the customer's profile
     * @throws CustomerNotFoundException if the customer does not exist
     */
    CustomerResponse getCustomerById(Long customerId);
}