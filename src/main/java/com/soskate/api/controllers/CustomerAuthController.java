package com.soskate.api.controllers;

import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.services.customer.auth.CustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for customer authentication.
 * Handles registration and email verification endpoints.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Tag(name = "Customer Auth", description = "Customer registration and authentication")
@RestController
@RequestMapping("/customer/auth")
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    /**
     * Registration endpoint for a new customer.
     *
     * @param customerToRegister the registration data (automatically validated)
     * @return the created customer with 201 CREATED status
     */
    @Operation(
            summary = "Register a customer",
            description = "Creates a new customer account"
    )
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegisterRequest customerToRegister) {

        log.info("Registration request received for email: {}", customerToRegister.email());

        CustomerResponse customer = customerAuthService.registerCustomer(customerToRegister);

        log.info("Registration successful for email: {}", customer.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    /**
     * Endpoint to check if an email already exists.
     * Useful for real-time frontend validation.
     *
     * @param email the email to check
     * @return true if the email already exists
     */
    @Operation(
            summary = "Check if an email exists",
            description = "Checks if an email is already used by a customer"
    )
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Checking email existence: {}", email);
        boolean exists = customerAuthService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}
