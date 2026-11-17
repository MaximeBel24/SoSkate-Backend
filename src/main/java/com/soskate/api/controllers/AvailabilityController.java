package com.soskate.api.controllers;

import com.soskate.api.dto.availability.*;
import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.services.availability.AvailabilityService;
import com.soskate.api.services.availability.SlotCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            summary = "List availabilities",
            description = "Ranges over a period, booked ones included. By default, from today to the end of the open period"
    )
    @GetMapping("/availabilities")
    public ResponseEntity<List<AvailabilityRangeResponse>> getAvailabilities(
            @PathVariable Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(availabilityService.getRanges(instructorId, from, to));
    }

    @Operation(
            summary = "Add availabilities",
            description = "Adds the same hours on one or several days. Only the missing blocks are created"
    )
    @PostMapping("/availabilities")
    @PreAuthorize("@userSecurity.isOwner(#instructorId) or @userSecurity.isAdmin()")
    public ResponseEntity<AvailabilityAddResponse> addAvailabilities(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityAddRequest request
    ) {
        log.info("POST /api/instructors/{}/availabilities - {} date(s)", instructorId, request.dates().size());
        return ResponseEntity.ok(availabilityService.add(instructorId, request));
    }

    @Operation(
            summary = "Repeat a week",
            description = "Repeats a week's schedule on the following weeks. Only the missing blocks are created"
    )
    @PostMapping("/availabilities/copy-week")
    @PreAuthorize("@userSecurity.isOwner(#instructorId) or @userSecurity.isAdmin()")
    public ResponseEntity<AvailabilityAddResponse> copyWeek(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityCopyWeekRequest request
    ) {
        log.info("POST /api/instructors/{}/availabilities/copy-week - {} on {} week(s)",
                instructorId, request.weekStart(), request.weeks());
        return ResponseEntity.ok(availabilityService.copyWeek(instructorId, request));
    }

    @Operation(
            summary = "Change a range",
            description = "Without confirmation, writes nothing if a lesson would be cancelled or a leftover erased"
    )
    @PutMapping("/availabilities")
    @PreAuthorize("@userSecurity.isOwner(#instructorId) or @userSecurity.isAdmin()")
    public ResponseEntity<AvailabilityChangeResponse> updateAvailability(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        log.info("PUT /api/instructors/{}/availabilities - {}", instructorId, request.date());
        return ResponseEntity.ok(availabilityService.update(instructorId, request));
    }

    @Operation(
            summary = "Remove availabilities",
            description = "Removes the same hours on one or several days. Without confirmation, writes nothing "
                    + "if a lesson would be cancelled or a leftover erased"
    )
    @PostMapping("/availabilities/removal")
    @PreAuthorize("@userSecurity.isOwner(#instructorId) or @userSecurity.isAdmin()")
    public ResponseEntity<AvailabilityChangeResponse> removeAvailabilities(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityRemovalRequest request
    ) {
        log.info("POST /api/instructors/{}/availabilities/removal - {} date(s)", instructorId, request.dates().size());
        return ResponseEntity.ok(availabilityService.remove(instructorId, request));
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
