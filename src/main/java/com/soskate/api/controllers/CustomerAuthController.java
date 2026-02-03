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

/**
 * Contrôleur REST pour l'authentification des customers.
 * Gère les endpoints d'inscription et de vérification d'email.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/customer/auth")
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    /**
     * Endpoint d'inscription d'un nouveau customer.
     *
     * @param customerToRegister les données d'inscription (validées automatiquement)
     * @return le customer créé avec le statut 201 CREATED
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CustomerRegisterRequest customerToRegister) {

        log.info("Requête d'inscription reçue pour l'email : {}", customerToRegister.email());

        CustomerResponse customer = customerAuthService.registerCustomer(customerToRegister);

        log.info("Inscription réussie pour l'email : {}", customer.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    /**
     * Endpoint pour vérifier si un email existe déjà.
     * Utile pour la validation côté frontend en temps réel.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);
        boolean exists = customerAuthService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}
