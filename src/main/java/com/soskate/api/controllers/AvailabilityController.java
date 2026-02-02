package com.soskate.api.controllers;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
import com.soskate.api.dto.availability.AvailableSlotResponse;
import com.soskate.api.services.instructor.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/instructors/{instructorId}/availabilities")
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityCreateRequest request
    ) {
        AvailabilityResponse response = availabilityService.createAvailability(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instructors/{instructorId}/availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<AvailabilityResponse> response;

        if (from != null && to != null) {
            response = availabilityService.getAvailabilityByInstructorAndDateRange(instructorId, from, to);
        } else {
            response = availabilityService.getAvailabilityByInstructor(instructorId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> getAvailabilityById(@PathVariable Long id) {
        AvailabilityResponse response = availabilityService.getAvailabilityById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/instructors/{instructorId}/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        AvailabilityResponse response = availabilityService.updateAvailability(instructorId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/instructors/{instructorId}/availabilities/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id
    ) {
        availabilityService.deleteAvailability(instructorId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/instructors/{instructorId}/available")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable Long instructorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(instructorId, from, to);
        return ResponseEntity.ok(response);
    }
}