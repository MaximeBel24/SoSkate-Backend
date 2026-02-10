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
@Tag(name = "Authentication", description = "User login management")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User login",
            description = "Authenticates a Customer or Instructor"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Unified login request received for email: {}", loginRequest.email());

        LoginResponse response = authService.login(loginRequest);

        log.info("Login successful - Email: {}, Role: {}",
                loginRequest.email(),
                response.role());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Check if an email exists",
            description = "Checks if an email is already used by a Customer or Instructor"
    )
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        log.debug("Checking email existence: {}", email);
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}