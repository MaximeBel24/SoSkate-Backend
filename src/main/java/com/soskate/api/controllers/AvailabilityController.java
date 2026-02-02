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
    private final SlotCalculationService slotCalculationService;

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

    /**
     * Récupère les plages horaires disponibles d'un instructeur sur une période.
     * Usage : Vue planning/calendrier pour l'instructeur.
     * Retourne des créneaux continus (plages horaires) après soustraction des bookings existants.
     * Ne prend pas en compte les spots ni les buffers de déplacement.
     *
     * @param instructorId ID de l'instructeur
     * @param from Date de début de la période
     * @param to Date de fin de la période
     * @return Liste des plages horaires disponibles (availabilityId, date, startTime, endTime)
     */
    @GetMapping("/instructors/{instructorId}/available")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @PathVariable Long instructorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<AvailableSlotResponse> response = availabilityService.getAvailableSlots(instructorId, from, to);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les créneaux réservables pour une date, un spot et une durée donnés.
     * Usage : Processus de réservation pour le client.
     * Retourne des créneaux discrets de durée fixe (tous les 30 min) avec leur statut (libre/occupé).
     * Prend en compte le buffer de déplacement entre spots (temps de trajet).
     *
     * @param instructorId ID de l'instructeur
     * @param spotId ID du spot où se déroulera la session
     * @param date Date souhaitée pour la réservation
     * @param durationMinutes Durée souhaitée de la session en minutes
     * @return Liste des créneaux avec statut libre/occupé (startTime, endTime, isFree)
     */
    @GetMapping("/instructors/{instructorId}/bookable")
    public ResponseEntity<AvailableSlotsResponse> getBookableSlots(
            @PathVariable Long instructorId,
            @RequestParam Long spotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationMinutes
    ) {
        AvailableSlotsResponse response = slotCalculationService.calculateAvailableSlots(
                instructorId,
                spotId,
                date,
                durationMinutes
        );
        return ResponseEntity.ok(response);
    }
}