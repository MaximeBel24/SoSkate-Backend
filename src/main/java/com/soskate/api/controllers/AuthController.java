package com.soskate.api.controllers;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.LoginResponse;
import com.soskate.api.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Gestion de la connexion utilisateur")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Connexion utilisateur",
            description = "Authentifie un Customer ou Instructor"
    )
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

    @Operation(
            summary = "Vérifier si un email existe",
            description = "Vérifie si un email est déjà utilisé par un Customer ou Instructor"
    )
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}