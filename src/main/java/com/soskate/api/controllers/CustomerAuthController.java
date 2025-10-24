package com.soskate.api.controllers;

import com.soskate.api.dtos.auth.login.LoginRequestDTO;
import com.soskate.api.dtos.auth.login.LoginResponseDTO;
import com.soskate.api.dtos.auth.register.CustomerRegisterRequestDTO;
import com.soskate.api.dtos.customer.CustomerResponseDTO;
import com.soskate.api.services.customer.auth.CustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification des customers.
 * Gère les endpoints d'inscription, connexion et gestion du mot de passe.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/customer/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Pour permettre les requêtes depuis le frontend (dev uniquement)
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    /**
     * Endpoint d'inscription d'un nouveau customer.
     *
     * @param customerToRegister les données d'inscription (validées automatiquement)
     * @return le customer créé avec le statut 201 CREATED
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> register(@Valid @RequestBody CustomerRegisterRequestDTO customerToRegister) {

        log.info("Requête d'inscription reçue pour l'email : {}", customerToRegister.email());

        CustomerResponseDTO customer = customerAuthService.registerCustomer(customerToRegister);

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

    /**
     * Endpoint de connexion pour un customer.
     * Version simplifiée sans JWT (pour le dev).
     *
     * @param loginRequest email + password (validés automatiquement)
     * @return les informations du customer avec message de succès
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {

        log.info("Requête de connexion reçue pour l'email : {}", loginRequest.email());

        LoginResponseDTO response = customerAuthService.login(loginRequest);

        log.info("Connexion réussie pour l'email : {}", loginRequest.email());

        return ResponseEntity.ok(response);
    }
}