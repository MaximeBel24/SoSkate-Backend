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

@Tag(name = "Availabilities", description = "Gestion des disponibilités des instructeurs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/instructors/{instructorId}")
@Slf4j
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final SlotCalculationService slotCalculationService;

    @Operation(
            summary = "Créer une disponibilité",
            description = "Ajoute une plage de disponibilité pour un instructeur"
    )
    @PostMapping("/availabilities")
    public ResponseEntity<AvailabilityResponse> createAvailability(
            @PathVariable Long instructorId,
            @Valid @RequestBody AvailabilityCreateRequest request
    ) {
        log.info("POST /api/instructors/{}/availabilities - Création d'une disponibilité", instructorId);
        AvailabilityResponse response = availabilityService.createAvailability(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Lister les disponibilités",
            description = "Récupère les disponibilités d'un instructeur (optionnel: plage de dates)"
    )
    @GetMapping("/availabilities")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilityByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("GET /api/instructors/{}/availabilities - Récupération des disponibilités", instructorId);
        List<AvailabilityResponse> response;

        if (from != null && to != null) {
            response = availabilityService.getAvailabilityByInstructorAndDateRange(instructorId, from, to);
        } else {
            response = availabilityService.getAvailabilityByInstructor(instructorId);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Modifier une disponibilité",
            description = "Met à jour une disponibilité existante"
    )
    @PutMapping("/availabilities/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        log.info("PUT /api/instructors/{}/availabilities/{} - Mise à jour", instructorId, id);
        AvailabilityResponse response = availabilityService.updateAvailability(instructorId, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer une disponibilité",
            description = "Supprime une disponibilité"
    )
    @DeleteMapping("/availabilities/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long instructorId,
            @PathVariable Long id
    ) {
        log.info("DELETE /api/instructors/{}/availabilities/{} - Suppression", instructorId, id);
        availabilityService.deleteAvailability(instructorId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Récupérer les créneaux libres",
            description = "Retourne les créneaux disponibles sur une période"
    )
    @GetMapping("/available")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable Long instructorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        log.info("GET /api/instructors/{}/available - Créneaux libres du {} au {}", instructorId, from, to);
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(instructorId, from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Récupérer les créneaux réservables",
            description = "Calcule les créneaux réservables pour un spot et une durée donnés"
    )
    @GetMapping("/bookable")
    public ResponseEntity<AvailableSlotsResponse> getBookableSlots(
            @PathVariable Long instructorId,
            @RequestParam Long spotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationMinutes
    ) {
        log.info("GET /api/instructors/{}/bookable - Créneaux réservables pour spotId={}, date={}, durée={}min",
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