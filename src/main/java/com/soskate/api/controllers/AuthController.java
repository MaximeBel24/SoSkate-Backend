package com.soskate.api.controllers;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.LoginResponse;
import com.soskate.api.services.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification unifiée.
 *
 * Point d'entrée unique pour la connexion de tous les types d'utilisateurs
 * (Customer et Instructor). Le service détecte automatiquement le type.
 *
 * L'ancien endpoint /api/customer/auth/login reste fonctionnel pour
 * la rétrocompatibilité.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints d'authentification unifiée")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de connexion pour Customer et Instructor.
     *
     * Le service détecte automatiquement le type d'utilisateur et retourne
     * les informations appropriées avec le rôle correspondant.
     *
     * @param loginRequest email + password (validés automatiquement)
     * @return les informations de l'utilisateur avec son rôle
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Requête de connexion unifiée reçue pour l'email : {}", loginRequest.email());

        LoginResponse response = authService.login(loginRequest);

        log.info("Connexion réussie - Email: {}, Rôle: {}",
                loginRequest.email(),
                response.role());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour vérifier si un email existe déjà (Customer OU Instructor).
     * Utile pour la validation côté frontend en temps réel.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}