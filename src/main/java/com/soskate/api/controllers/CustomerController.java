package com.soskate.api.controllers;

import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.dto.customer.CustomerUpdateRequest;
import com.soskate.api.services.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Customer profile management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile", description = "Customer profile management")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Retrieve profile",
            description = "Retrieves a customer's profile"
    )
    @GetMapping("/{customerId}")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer ID")
            @PathVariable Long customerId) {

        log.info("GET profile request for customer ID: {}", customerId);

        CustomerResponse response = customerService.getCustomerById(customerId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update profile",
            description = "Updates a customer's profile (partial)"
    )
    @PutMapping("/{customerId}")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer ID")
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {

        log.info("PUT profile request for customer ID: {}", customerId);

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        log.info("Profile successfully updated for customer ID: {}", customerId);

        return ResponseEntity.ok(response);
    }
}