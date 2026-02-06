package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.*;
import com.soskate.api.services.instructor.InstructorAdminService;
import com.soskate.api.services.instructor.InstructorQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST Controller for admin operations on instructors.
 * TODO: Secure with @PreAuthorize("hasRole('ADMIN')") when Spring Security is implemented
 */
@Tag(name = "Admin - Instructors", description = "Gestion administrative des instructeurs")
@RestController
@RequestMapping("/admin/instructors")
@RequiredArgsConstructor
@Slf4j
public class AdminInstructorController {

    private final InstructorAdminService adminService;
    private final InstructorQueryService queryService;

    @Operation(
            summary = "Créer un instructeur",
            description = "Crée un instructeur et envoie un email d'invitation"
    )
    @PostMapping
    public ResponseEntity<InstructorResponse> createInstructor(
            @Valid @RequestBody InstructorCreateRequest request) {
        log.info("POST /api/admin/instructors - Creating instructor: {}", request.email());
        InstructorResponse created = adminService.createInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Lister tous les instructeurs",
            description = "Récupère tous les instructeurs (tous statuts)"
    )
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors() {
        log.info("GET /api/admin/instructors - Fetching all instructors");
        return ResponseEntity.ok(queryService.getAllInstructors());
    }

    @Operation(
            summary = "Récupérer un instructeur",
            description = "Récupère un instructeur par son ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/admin/instructors/{} - Fetching instructor", id);
        return ResponseEntity.ok(queryService.getInstructorById(id));
    }

    @Operation(
            summary = "Lister les invitations en attente",
            description = "Récupère les instructeurs avec invitations en attente"
    )
    @GetMapping("/pending")
    public ResponseEntity<List<InstructorResponse>> getPendingInvitations() {
        log.info("GET /api/admin/instructors/pending - Fetching pending invitations");
        return ResponseEntity.ok(adminService.getPendingInvitations());
    }

    @Operation(
            summary = "Lister les invitations expirées",
            description = "Récupère les instructeurs avec invitations expirées"
    )
    @GetMapping("/expired")
    public ResponseEntity<List<InstructorResponse>> getExpiredInvitations() {
        log.info("GET /api/admin/instructors/expired - Fetching expired invitations");
        return ResponseEntity.ok(adminService.getExpiredInvitations());
    }

    // ==================== Update Operations ====================

    @Operation(
            summary = "Renvoyer une invitation",
            description = "Renvoie l'email d'invitation avec un nouveau token"
    )
    @PatchMapping("/{id}/resend")
    public ResponseEntity<InstructorResponse> resendInvitation(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/resend - Resending invitation", id);
        return ResponseEntity.ok(adminService.resendInvitation(id));
    }

    @Operation(
            summary = "Suspendre un instructeur",
            description = "Suspend le compte d'un instructeur"
    )
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<InstructorResponse> suspendInstructor(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/suspend - Suspending instructor", id);
        return ResponseEntity.ok(adminService.suspendInstructor(id));
    }

    @Operation(
            summary = "Réactiver un instructeur",
            description = "Réactive un compte instructeur suspendu"
    )
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<InstructorResponse> reactivateInstructor(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/reactivate - Reactivating instructor", id);
        return ResponseEntity.ok(adminService.reactivateInstructor(id));
    }

    // ==================== Delete Operations ====================

    @Operation(
            summary = "Supprimer un instructeur",
            description = "Supprime définitivement un instructeur"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructorById(@PathVariable Long id) {
        log.info("DELETE /api/admin/instructors/{} - Deleting instructor", id);
        adminService.deleteInstructorById(id);
        return ResponseEntity.noContent().build();
    }
}
