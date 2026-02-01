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

    /**
     * Récupère le profil d'un customer.
     *
     * @param customerId l'ID du customer
     * @return le profil du customer
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "ID du customer")
            @PathVariable Long customerId) {

        log.info("Requête GET profil pour customer ID: {}", customerId);

        CustomerResponse response = customerService.getCustomerById(customerId);

        return ResponseEntity.ok(response);
    }

    /**
     * Met à jour le profil d'un customer.
     * Seuls les champs fournis sont mis à jour (mise à jour partielle).
     *
     * @param customerId l'ID du customer à mettre à jour
     * @param request les données à mettre à jour
     * @return le customer mis à jour
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomerProfile(
            @Parameter(description = "ID du customer")
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {

        log.info("Requête PUT profil pour customer ID: {}", customerId);

        CustomerResponse response = customerService.updateCustomer(customerId, request);

        log.info("Profil mis à jour avec succès pour customer ID: {}", customerId);

        return ResponseEntity.ok(response);
    }
}