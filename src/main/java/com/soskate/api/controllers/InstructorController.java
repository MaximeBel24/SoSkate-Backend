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
@Tag(name = "Instructors", description = "Instructor browsing and activation")
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
            summary = "List active instructors",
            description = "Retrieves all active instructors for customers"
    )
    @GetMapping
    public ResponseEntity<List<InstructorSummary>> getActiveInstructors() {
        log.info("GET /api/instructors - Fetching active instructors");
        return ResponseEntity.ok(queryService.getActiveInstructors());
    }

    @Operation(
            summary = "Retrieve an instructor",
            description = "Retrieves an instructor's public profile"
    )
    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructorById(@PathVariable Long id) {
        log.info("GET /api/instructors/{} - Fetching instructor profile", id);
        return ResponseEntity.ok(queryService.getInstructorById(id));
    }

    @Operation(
            summary = "Filter by specialty",
            description = "Retrieves instructors by skate specialty"
    )
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpecialty(@PathVariable SkateSpecialty specialty) {
        log.info("GET /api/instructors/specialty/{} - Fetching instructors by specialty", specialty);
        return ResponseEntity.ok(queryService.getInstructorsBySpecialty(specialty));
    }

    @Operation(
            summary = "Validate an activation token",
            description = "Checks if an activation token is valid"
    )
    @GetMapping("/activate/validate")
    public ResponseEntity<Map<String, Boolean>> validateActivationToken(@RequestParam("token") String token) {
        log.info("GET /api/instructors/activate/validate - Validating token");
        boolean isValid = activationService.validateActivationToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @Operation(
            summary = "Activate an instructor account",
            description = "Activates the account with the chosen password"
    )
    @PostMapping("/activate")
    public ResponseEntity<InstructorResponse> activateAccount(@Valid @RequestBody InstructorActivateRequest request) {
        log.info("POST /api/instructors/activate - Activating account");
        return ResponseEntity.ok(activationService.activateAccount(request));
    }

    @Operation(
            summary = "Update profile",
            description = "Updates the instructor's profile"
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
            summary = "Retrieve bookings",
            description = "Retrieves an instructor's bookings (filter: upcoming, passed)"
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
