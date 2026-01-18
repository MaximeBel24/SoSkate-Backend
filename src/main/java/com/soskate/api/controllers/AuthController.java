package com.soskate.api.controllers;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.UnifiedLoginResponse;
import com.soskate.api.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
     * Endpoint de connexion unifié pour Customer et Instructor.
     *
     * Le service détecte automatiquement le type d'utilisateur et retourne
     * les informations appropriées avec le rôle correspondant.
     *
     * @param loginRequest email + password (validés automatiquement)
     * @return les informations de l'utilisateur avec son rôle
     */
    @Operation(
            summary = "Connexion unifiée",
            description = "Authentifie un utilisateur (Customer ou Instructor) et retourne ses informations avec son rôle"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = UnifiedLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Email ou mot de passe incorrect",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de requête invalides",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<UnifiedLoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Requête de connexion unifiée reçue pour l'email : {}", loginRequest.email());

        UnifiedLoginResponse response = authService.login(loginRequest);

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
    @Operation(
            summary = "Vérifier l'existence d'un email",
            description = "Vérifie si un email est déjà utilisé par un Customer ou un Instructor"
    )
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}