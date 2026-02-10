package com.soskate.api.controllers;

import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.dto.instructor.*;
import com.soskate.api.enums.SkateSpecialty;
import com.soskate.api.services.booking.BookingService;
import com.soskate.api.services.instructor.InstructorActivationService;
import com.soskate.api.services.instructor.InstructorQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for public instructor operations.
 * Includes:
 * - Public listing of active instructors (for customers)
 * - Account activation (for invited instructors)
 */
@Tag(name = "Instructors", description = "Consultation et activation des instructeurs")
@RestController
@RequestMapping("/instructors")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {

    private final InstructorQueryService queryService;
    private final InstructorActivationService activationService;
    private final BookingService bookingService;

    // ==================== Public Listing ====================

    @Operation(
            summary = "Lister les instructeurs actifs",
            description = "Récupère tous les instructeurs actifs pour les clients"
    )
    @GetMapping
    public ResponseEntity<List<InstructorSummary>> getActiveInstructors() {
        log.info("GET /api/instructors - Fetching active instructors");
        return ResponseEntity.ok(queryService.getActiveInstructors());
    }

    @Operation(
            summary = "Récupérer un instructeur",
            description = "Récupère le profil public d'un instructeur"
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/instructors/{} - Fetching instructor profile", id);
        return ResponseEntity.ok(queryService.getInstructorById(id));
    }

    @Operation(
            summary = "Filtrer par spécialité",
            description = "Récupère les instructeurs par spécialité de skate"
    )
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpecialty(@PathVariable SkateSpecialty specialty) {
        log.info("GET /api/instructors/specialty/{} - Fetching instructors by specialty", specialty);
        return ResponseEntity.ok(queryService.getInstructorsBySpecialty(specialty));
    }

    @Operation(
            summary = "Valider un token d'activation",
            description = "Vérifie si un token d'activation est valide"
    )
    @GetMapping("/activate/validate")
    public ResponseEntity<Map<String, Boolean>> validateActivationToken(@RequestParam("token") String token) {
        log.info("GET /api/instructors/activate/validate - Validating token");
        boolean isValid = activationService.validateActivationToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @Operation(
            summary = "Activer un compte instructeur",
            description = "Active le compte avec le mot de passe choisi"
    )
    @PostMapping("/activate")
    public ResponseEntity<InstructorResponse> activateAccount(@Valid @RequestBody InstructorActivateRequest request) {
        log.info("POST /api/instructors/activate - Activating account");
        return ResponseEntity.ok(activationService.activateAccount(request));
    }

    @Operation(
            summary = "Mettre à jour le profil",
            description = "Met à jour le profil de l'instructeur"
    )
    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(#id) or @userSecurity.isAdmin()")
    public ResponseEntity<InstructorResponse> updateInstructor(
            @PathVariable Long id,
            @Valid @RequestBody InstructorUpdateRequest request) {
        log.info("PUT /api/instructors/{}/profile - Updating instructor profile", id);
        return ResponseEntity.ok(queryService.updateInstructor(id, request));
    }

    @Operation(
            summary = "Récupérer les réservations",
            description = "Récupère les réservations d'un instructeur (filtre: upcoming, passed)"
    )
    @GetMapping("/{instructorId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String filter
    ) {
        List<BookingResponse> response = switch(filter) {
            case "upcoming" -> bookingService.getUpcomingBookingsByInstructor(instructorId);
            case "passed" -> bookingService.getPassedBookingsByInstructor(instructorId);
            case null, default -> bookingService.getBookingsByInstructor(instructorId);
        };

        return ResponseEntity.ok(response);
    }
}
