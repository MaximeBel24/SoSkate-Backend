package com.soskate.api.controllers.admin;

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
@Tag(name = "Admin - Instructors", description = "Administrative instructor management")
@RestController
@RequestMapping("/admin/instructors")
@RequiredArgsConstructor
@Slf4j
public class AdminInstructorController {

    private final InstructorAdminService adminService;
    private final InstructorQueryService queryService;

    @Operation(
            summary = "Create an instructor",
            description = "Creates an instructor and sends an invitation email"
    )
    @PostMapping
    public ResponseEntity<InstructorResponse> createInstructor(
            @Valid @RequestBody InstructorCreateRequest request) {
        log.info("POST /api/admin/instructors - Creating instructor: {}", request.email());
        InstructorResponse created = adminService.createInstructor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "List all instructors",
            description = "Retrieves all instructors (all statuses)"
    )
    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getAllInstructors() {
        log.info("GET /api/admin/instructors - Fetching all instructors");
        return ResponseEntity.ok(queryService.getAllInstructors());
    }

    @Operation(
            summary = "Retrieve an instructor",
            description = "Retrieves an instructor by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/admin/instructors/{} - Fetching instructor", id);
        return ResponseEntity.ok(queryService.getInstructorById(id));
    }

    @Operation(
            summary = "List pending invitations",
            description = "Retrieves instructors with pending invitations"
    )
    @GetMapping("/pending")
    public ResponseEntity<List<InstructorResponse>> getPendingInvitations() {
        log.info("GET /api/admin/instructors/pending - Fetching pending invitations");
        return ResponseEntity.ok(adminService.getPendingInvitations());
    }

    @Operation(
            summary = "List expired invitations",
            description = "Retrieves instructors with expired invitations"
    )
    @GetMapping("/expired")
    public ResponseEntity<List<InstructorResponse>> getExpiredInvitations() {
        log.info("GET /api/admin/instructors/expired - Fetching expired invitations");
        return ResponseEntity.ok(adminService.getExpiredInvitations());
    }

    // ==================== Update Operations ====================

    @Operation(
            summary = "Resend an invitation",
            description = "Resends the invitation email with a new token"
    )
    @PatchMapping("/{id}/resend")
    public ResponseEntity<InstructorResponse> resendInvitation(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/resend - Resending invitation", id);
        return ResponseEntity.ok(adminService.resendInvitation(id));
    }

    @Operation(
            summary = "Suspend an instructor",
            description = "Suspends an instructor's account"
    )
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<InstructorResponse> suspendInstructor(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/suspend - Suspending instructor", id);
        return ResponseEntity.ok(adminService.suspendInstructor(id));
    }

    @Operation(
            summary = "Reactivate an instructor",
            description = "Reactivates a suspended instructor account"
    )
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<InstructorResponse> reactivateInstructor(@PathVariable Long id) {
        log.info("PATCH /api/admin/instructors/{}/reactivate - Reactivating instructor", id);
        return ResponseEntity.ok(adminService.reactivateInstructor(id));
    }

    // ==================== Delete Operations ====================

    @Operation(
            summary = "Delete an instructor",
            description = "Permanently deletes an instructor"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstructorById(@PathVariable Long id) {
        log.info("DELETE /api/admin/instructors/{} - Deleting instructor", id);
        adminService.deleteInstructorById(id);
        return ResponseEntity.noContent().build();
    }
}
