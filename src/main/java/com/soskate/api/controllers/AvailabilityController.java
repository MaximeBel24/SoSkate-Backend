package com.soskate.api.controllers;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
import com.soskate.api.dto.availability.AvailableSlotResponse;
import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.services.availability.SlotCalculationService;
import com.soskate.api.services.availability.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Availabilities", description = "Instructor availability management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/instructors/{instructorId}")
@Slf4j
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final SlotCalculationService slotCalculationService;

    @Operation(
            summary = "Create an availability",
            description = "Adds an availability slot for an instructor"
    )
    @PostMapping("/availabilities")
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityCreateRequest request
    ) {
        log.info("POST /api/instructors/{}/availabilities - Creating an availability", instructorId);
        AvailabilityResponse response = availabilityService.createAvailability(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "List availabilities",
            description = "Retrieves an instructor's availabilities (optional: date range)"
    )
    @GetMapping("/availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("GET /api/instructors/{}/availabilities - Retrieving availabilities", instructorId);
        List<AvailabilityResponse> response;

        if (from != null && to != null) {
            response = availabilityService.getAvailabilityByInstructorAndDateRange(instructorId, from, to);
        } else {
            response = availabilityService.getAvailabilityByInstructor(instructorId);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update an availability",
            description = "Updates an existing availability"
    )
    @PutMapping("/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        log.info("PUT /api/instructors/{}/availabilities/{} - Updating", instructorId, id);
        AvailabilityResponse response = availabilityService.updateAvailability(instructorId, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete an availability",
            description = "Deletes an availability"
    )
    @DeleteMapping("/availabilities/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id
    ) {
        log.info("DELETE /api/instructors/{}/availabilities/{} - Deleting", instructorId, id);
        availabilityService.deleteAvailability(instructorId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Retrieve available slots",
            description = "Returns available slots over a period"
    )
    @GetMapping("/available")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable Long instructorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("GET /api/instructors/{}/available - Available slots from {} to {}", instructorId, from, to);
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(instructorId, from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve bookable slots",
            description = "Calculates bookable slots for a given spot and duration"
    )
    @GetMapping("/bookable")
    public ResponseEntity<AvailableSlotsResponse> getBookableSlots(
            @PathVariable Long instructorId,
            @RequestParam Long spotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationMinutes
    ) {
        log.info("GET /api/instructors/{}/bookable - Bookable slots for spotId={}, date={}, duration={}min",
                instructorId, spotId, date, durationMinutes);
        AvailableSlotsResponse response = slotCalculationService.calculateAvailableSlots(
                instructorId,
                spotId,
                date,
                durationMinutes
        );
        return ResponseEntity.ok(response);
    }
}