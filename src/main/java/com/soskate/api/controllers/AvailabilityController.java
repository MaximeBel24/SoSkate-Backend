package com.soskate.api.controllers;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
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
    public ResponseEntity<AvailabilityResponse> create(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityCreateRequest request
    ) {
        AvailabilityResponse response = availabilityService.create(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instructors/{instructorId}/availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<AvailabilityResponse> response;

        if (from != null && to != null) {
            response = availabilityService.getByInstructorAndDateRange(instructorId, from, to);
        } else {
            response = availabilityService.getByInstructor(instructorId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> getById(@PathVariable Long id) {
        AvailabilityResponse response = availabilityService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/instructors/{instructorId}/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> update(
            @PathVariable Long instructorId,
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        AvailabilityResponse response = availabilityService.update(instructorId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/instructors/{instructorId}/availabilities/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long instructorId,
            @PathVariable Long id
    ) {
        availabilityService.delete(instructorId, id);
        return ResponseEntity.noContent().build();
    }
}