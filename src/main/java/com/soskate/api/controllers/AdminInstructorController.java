package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.*;
import com.soskate.api.services.instructor.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for admin operations on instructors.
 *
 * TODO: Secure with @PreAuthorize("hasRole('ADMIN')") when Spring Security is implemented
 */
@RestController
@RequestMapping("/admin/instructors")
@RequiredArgsConstructor
@Slf4j
public class AdminInstructorController {

    private final InstructorService instructorService;

    // ==================== Create Operations ====================

    /**
     * Creates a new instructor and sends an invitation email.
     *
     * POST /api/admin/instructors
     */
    @PostMapping
    public ResponseEntity<InstructorResponse> createInstructor(
            @Valid @RequestBody InstructorCreateRequest request) {
        log.info("POST /api/admin/instructors - Creating instructor: {}", request.email());
        InstructorResponse created = instructorService.createInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ==================== Read Operations ====================

    /**
     * Gets all instructors (all statuses).
     *
     * GET /api/admin/instructors
     */
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors() {
        log.info("GET /api/admin/instructors - Fetching all instructors");
        return ResponseEntity.ok(instructorService.getAllInstructors());
    }

    /**
     * Gets a specific instructor by ID.
     *
     * GET /api/admin/instructors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/admin/instructors/{} - Fetching instructor", id);
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    /**
     * Gets all instructors with pending invitations.
     *
     * GET /api/admin/instructors/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<InstructorResponse>> getPendingInvitations() {
        log.info("GET /api/admin/instructors/pending - Fetching pending invitations");
        return ResponseEntity.ok(instructorService.getPendingInvitations());
    }

    /**
     * Gets all instructors with expired invitations.
     *
     * GET /api/admin/instructors/expired
     */
    @GetMapping("/expired")
    public ResponseEntity<List<InstructorResponse>> getExpiredInvitations() {
        log.info("GET /api/admin/instructors/expired - Fetching expired invitations");
        return ResponseEntity.ok(instructorService.getExpiredInvitations());
    }

    // ==================== Update Operations ====================

    /**
     * Updates an instructor's profile (admin can edit any field).
     *
     * PUT /api/admin/instructors/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponse> updateInstructor(
            @PathVariable Long id,
            @Valid @RequestBody InstructorUpdateRequest request) {
        log.info("PUT /api/admin/instructors/{} - Updating instructor", id);
        return ResponseEntity.ok(instructorService.updateProfile(id, request));
    }

    /**
     * Resends the invitation email to an instructor.
     * Generates a new token and resets the expiration timer.
     *
     * POST /api/admin/instructors/{id}/resend-invitation
     */
    @PostMapping("/{id}/resend-invitation")
    public ResponseEntity<InstructorResponse> resendInvitation(@PathVariable Long id) {
        log.info("POST /api/admin/instructors/{}/resend-invitation - Resending invitation", id);
        return ResponseEntity.ok(instructorService.resendInvitation(id));
    }

    /**
     * Suspends an instructor account.
     *
     * POST /api/admin/instructors/{id}/suspend
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<InstructorResponse> suspendInstructor(@PathVariable Long id) {
        log.info("POST /api/admin/instructors/{}/suspend - Suspending instructor", id);
        return ResponseEntity.ok(instructorService.suspendInstructor(id));
    }

    /**
     * Reactivates a suspended instructor account.
     *
     * POST /api/admin/instructors/{id}/reactivate
     */
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<InstructorResponse> reactivateInstructor(@PathVariable Long id) {
        log.info("POST /api/admin/instructors/{}/reactivate - Reactivating instructor", id);
        return ResponseEntity.ok(instructorService.reactivateInstructor(id));
    }

    // ==================== Delete Operations ====================

    /**
     * Deletes an instructor.
     *
     * DELETE /api/admin/instructors/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id) {
        log.info("DELETE /api/admin/instructors/{} - Deleting instructor", id);
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }
}