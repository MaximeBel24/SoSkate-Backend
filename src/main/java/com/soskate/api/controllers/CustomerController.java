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
 * Contrôleur REST pour la gestion du profil Customer.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile", description = "Gestion du profil utilisateur Customer")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Récupérer le profil",
            description = "Récupère le profil d'un client"
    )
    @GetMapping("/{customerId}")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "ID du customer")
            @PathVariable Long customerId) {

        log.info("Requête GET profil pour customer ID: {}", customerId);

        CustomerResponse response = customerService.getCustomerById(customerId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour le profil",
            description = "Met à jour le profil d'un client (partiel)"
    )
    @PutMapping("/{customerId}")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "ID du customer")
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {

        log.info("Requête PUT profil pour customer ID: {}", customerId);

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        log.info("Profil mis à jour avec succès pour customer ID: {}", customerId);

        return ResponseEntity.ok(response);
    }
}