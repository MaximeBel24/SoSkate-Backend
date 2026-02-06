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
 * Contrôleur REST pour la gestion des spots de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Tag(name = "Spots", description = "Gestion des spots de skateboard")
@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
@Slf4j
public class SpotController {

    private final SpotService spotService;
    private final InstructorSpotService instructorSpotService;

    @Operation(
            summary = "Créer un spot",
            description = "Crée un nouveau spot de skateboard"
    )
    @PostMapping
    public ResponseEntity<SpotResponse> createSpot(
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("Requête POST /api/spots - Création d'un spot : {}", requestDTO.name());

        SpotResponse spot = spotService.createSpot(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(spot);
    }

    @Operation(
            summary = "Lister tous les spots",
            description = "Récupère tous les spots"
    )
    @GetMapping
    public ResponseEntity<List<SpotResponse>> getAllSpots() {
        log.info("Requête GET /api/spots - Récupération de tous les spots");

        List<SpotResponse> spots = spotService.getAllSpots();

        log.info("{} spot(s) trouvé(s)", spots.size());

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Lister les spots actifs",
            description = "Récupère uniquement les spots actifs"
    )
    @GetMapping("/active")
    public ResponseEntity<List<SpotResponse>> getActiveSpots() {
        log.info("Requête GET /api/spots/active - Récupération des spots actifs");

        List<SpotResponse> spots = spotService.getActiveSpots();

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Récupérer un spot",
            description = "Récupère un spot par son ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getSpotById(@PathVariable Long id) {
        log.info("Requête GET /api/spots/{} - Récupération du spot", id);

        SpotResponse spot = spotService.getSpotById(id);

        return ResponseEntity.ok(spot);
    }

    @Operation(
            summary = "Filtrer par ville",
            description = "Récupère les spots d'une ville"
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<List<SpotResponse>> getSpotsByCity(@PathVariable String city) {
        log.info("Requête GET /api/spots/city/{} - Récupération par ville", city);

        List<SpotResponse> spots = spotService.getSpotsByCity(city);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Filtrer par type",
            description = "Récupère les spots indoor ou outdoor"
    )
    @GetMapping("/type")
    public ResponseEntity<List<SpotResponse>> getSpotsByType(
            @RequestParam(name = "indoor") Boolean isIndoor) {

        log.info("Requête GET /api/spots/type?indoor={} - Récupération par type", isIndoor);

        List<SpotResponse> spots = spotService.getSpotsByType(isIndoor);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Spots à proximité",
            description = "Récupère les spots dans un rayon GPS (pour la carte)"
    )
    @GetMapping("/nearby")
    public ResponseEntity<List<SpotResponse>> getSpotsNearby(
            @RequestParam(name = "lat") BigDecimal lat,
            @RequestParam(name = "lng") BigDecimal lng,
            @RequestParam(name = "radius", defaultValue = "10") double radius) {

        log.info("Requête GET /api/spots/nearby?lat={}&lng={}&radius={}", lat, lng, radius);

        List<SpotResponse> spots = spotService.getSpotsNearby(lat, lng, radius);

        return ResponseEntity.ok(spots);
    }

    @Operation(
            summary = "Modifier un spot",
            description = "Met à jour un spot existant"
    )
    @PutMapping("/{id}")
    public ResponseEntity<SpotResponse> updateSpot(
            @PathVariable Long id,
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("Requête PUT /api/spots/{} - Mise à jour du spot", id);

        SpotResponse spot = spotService.updateSpot(id, requestDTO);

        return ResponseEntity.ok(spot);
    }

    @Operation(
            summary = "Désactiver un spot",
            description = "Désactive un spot (soft delete)"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSpot(@PathVariable Long id) {
        log.info("Requête PATCH /api/spots/{}/deactivate - Désactivation", id);

        spotService.deactivateSpot(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprimer un spot",
            description = "Supprime définitivement un spot"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        log.info("Requête DELETE /api/spots/{} - Suppression", id);

        spotService.deleteSpot(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Instructeurs d'un spot",
            description = "Récupère les instructeurs associés à un spot"
    )
    @GetMapping("/{spotId}/instructors")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpot(@PathVariable Long spotId) {
        List<InstructorSummary> instructors = instructorSpotService.getInstructorsBySpot(spotId);
        return ResponseEntity.ok(instructors);
    }
}