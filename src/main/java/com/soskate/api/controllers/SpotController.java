package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;
import com.soskate.api.services.instructor.InstructorSpotService;
import com.soskate.api.services.spot.SpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for skateboard spot management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Tag(name = "Spots", description = "Skateboard spot management")
@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
@Slf4j
public class SpotController {

    private final SpotService spotService;
    private final InstructorSpotService instructorSpotService;

    @Operation(
            summary = "Create a spot",
            description = "Creates a new skateboard spot"
    )
    @PostMapping
    public ResponseEntity<SpotResponse> createSpot(
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("POST /api/spots - Creating a spot: {}", requestDTO.name());

        SpotResponse spot = spotService.createSpot(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(spot);
    }

    @Operation(
            summary = "List all spots",
            description = "Retrieves all spots"
    )
    @GetMapping
    public ResponseEntity<List<SpotResponse>> getAllSpots() {
        log.info("GET /api/spots - Retrieving all spots");

        List<SpotResponse> spots = spotService.getAllSpots();

        log.info("{} spot(s) found", spots.size());

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "List active spots",
            description = "Retrieves only active spots"
    )
    @GetMapping("/active")
    public ResponseEntity<List<SpotResponse>> getActiveSpots() {
        log.info("GET /api/spots/active - Retrieving active spots");

        List<SpotResponse> spots = spotService.getActiveSpots();

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Retrieve a spot",
            description = "Retrieves a spot by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getSpotById(@PathVariable Long id) {
        log.info("GET /api/spots/{} - Retrieving spot", id);

        SpotResponse spot = spotService.getSpotById(id);

        return ResponseEntity.ok(spot);
    }

    @Operation(
            summary = "Filter by city",
            description = "Retrieves spots in a city"
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<List<SpotResponse>> getSpotsByCity(@PathVariable String city) {
        log.info("GET /api/spots/city/{} - Retrieving by city", city);

        List<SpotResponse> spots = spotService.getSpotsByCity(city);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Filter by type",
            description = "Retrieves indoor or outdoor spots"
    )
    @GetMapping("/type")
    public ResponseEntity<List<SpotResponse>> getSpotsByType(
            @RequestParam(name = "indoor") Boolean isIndoor) {

        log.info("GET /api/spots/type?indoor={} - Retrieving by type", isIndoor);

        List<SpotResponse> spots = spotService.getSpotsByType(isIndoor);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Nearby spots",
            description = "Retrieves spots within a GPS radius (for the map)"
    )
    @GetMapping("/nearby")
    public ResponseEntity<List<SpotResponse>> getSpotsNearby(
            @RequestParam(name = "lat") BigDecimal lat,
            @RequestParam(name = "lng") BigDecimal lng,
            @RequestParam(name = "radius", defaultValue = "10") double radius) {

        log.info("GET /api/spots/nearby?lat={}&lng={}&radius={}", lat, lng, radius);

        List<SpotResponse> spots = spotService.getSpotsNearby(lat, lng, radius);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Update a spot",
            description = "Updates an existing spot"
    )
    @PutMapping("/{id}")
    public ResponseEntity<SpotResponse> updateSpot(
            @PathVariable Long id,
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("PUT /api/spots/{} - Updating spot", id);

        SpotResponse spot = spotService.updateSpot(id, requestDTO);

        return ResponseEntity.ok(spot);
    }

    @Operation(
            summary = "Deactivate a spot",
            description = "Deactivates a spot (soft delete)"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSpot(@PathVariable Long id) {
        log.info("PATCH /api/spots/{}/deactivate - Deactivating", id);

        spotService.deactivateSpot(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete a spot",
            description = "Permanently deletes a spot"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        log.info("DELETE /api/spots/{} - Deleting", id);

        spotService.deleteSpot(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Instructors of a spot",
            description = "Retrieves the instructors associated with a spot"
    )
    @GetMapping("/{spotId}/instructors")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpot(@PathVariable Long spotId) {
        List<InstructorSummary> instructors = instructorSpotService.getInstructorsBySpot(spotId);
        return ResponseEntity.ok(instructors);
    }
}
