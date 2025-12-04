package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.*;
import com.soskate.api.enums.SkateSpecialty;
import com.soskate.api.services.instructor.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for public instructor operations.
 *
 * Includes:
 * - Public listing of active instructors (for customers)
 * - Account activation (for invited instructors)
 */
@RestController
@RequestMapping("/instructors")
@RequiredArgsConstructor
@Slf4j
public class InstructorController {

    private final InstructorService instructorService;

    // ==================== Public Listing ====================

    /**
     * Gets all active instructors.
     * Used by customers to browse available instructors.
     *
     * GET /api/instructors
     */
    @GetMapping
    public ResponseEntity<List<InstructorSummary>> getActiveInstructors() {
        log.info("GET /api/instructors - Fetching active instructors");
        return ResponseEntity.ok(instructorService.getActiveInstructors());
    }

    /**
     * Gets an active instructor by ID.
     * Returns full profile for public viewing.
     *
     * GET /api/instructors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/instructors/{} - Fetching instructor profile", id);
        return ResponseEntity.ok(instructorService.getInstructorById(id));
    }

    /**
     * Gets instructors filtered by specialty.
     *
     * GET /api/instructors/specialty/{specialty}
     */
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpecialty(@PathVariable SkateSpecialty specialty) {
        log.info("GET /api/instructors/specialty/{} - Fetching instructors by specialty", specialty);
        return ResponseEntity.ok(instructorService.getInstructorsBySpecialty(specialty));
    }

    /**
     * Searches instructors by name.
     *
     * GET /api/instructors/search?q=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<InstructorSummary>> searchInstructors(@RequestParam("q") String query) {
        log.info("GET /api/instructors/search?q={} - Searching instructors", query);
        return ResponseEntity.ok(instructorService.searchInstructors(query));
    }

    // ==================== Account Activation ====================

    /**
     * Validates an activation token.
     * Used by frontend to check if a token is valid before showing the form.
     *
     * GET /api/instructors/activate/validate?token=...
     */
    @GetMapping("/activate/validate")
    public ResponseEntity<Map<String, Boolean>> validateActivationToken(@RequestParam("token") String token) {
        log.info("GET /api/instructors/activate/validate - Validating token");
        boolean isValid = instructorService.validateActivationToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    /**
     * Activates an instructor account.
     * Called when the instructor submits the activation form with their new password.
     *
     * POST /api/instructors/activate
     */
    @PostMapping("/activate")
    public ResponseEntity<InstructorResponse> activateAccount(@Valid @RequestBody InstructorActivateRequest request) {
        log.info("POST /api/instructors/activate - Activating account");
        return ResponseEntity.ok(instructorService.activateAccount(request));
    }

    // ==================== Authenticated Instructor Operations ====================
    // TODO: These endpoints should be secured with @PreAuthorize to ensure
    // only the instructor themselves can update their own profile

    /**
     * Updates the authenticated instructor's profile.
     *
     * PUT /api/instructors/{id}/profile
     *
     * TODO: Replace {id} with authentication context when Spring Security is implemented
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<InstructorResponse> updateMyProfile(@PathVariable Long id, @Valid @RequestBody InstructorUpdateRequest request) {
        log.info("PUT /api/instructors/{}/profile - Updating instructor profile", id);
        // TODO: Verify that the authenticated user matches the ID
        return ResponseEntity.ok(instructorService.updateProfile(id, request));
    }
}