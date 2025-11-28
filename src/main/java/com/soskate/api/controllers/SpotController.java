package com.soskate.api.controllers;

import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;
import com.soskate.api.services.spot.SpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des spots de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SpotController {

    private final SpotService spotService;

    /**
     * Crée un nouveau spot.
     *
     * @param requestDTO les données du spot
     * @return le spot créé avec statut 201 CREATED
     */
    @PostMapping
    public ResponseEntity<SpotResponse> createSpot(
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("Requête POST /api/spots - Création d'un spot : {}", requestDTO.name());

        SpotResponse spot = spotService.createSpot(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(spot);
    }

    /**
     * Récupère tous les spots.
     *
     * @return liste de tous les spots
     */
    @GetMapping
    public ResponseEntity<List<SpotResponse>> getAllSpots() {
        log.info("Requête GET /api/spots - Récupération de tous les spots");

        List<SpotResponse> spots = spotService.getAllSpots();

        log.info("{} spot(s) trouvé(s)", spots.size());

        return ResponseEntity.ok(spots);
    }

    /**
     * Récupère uniquement les spots actifs.
     *
     * @return liste des spots actifs
     */
    @GetMapping("/active")
    public ResponseEntity<List<SpotResponse>> getActiveSpots() {
        log.info("Requête GET /api/spots/active - Récupération des spots actifs");

        List<SpotResponse> spots = spotService.getActiveSpots();

        return ResponseEntity.ok(spots);
    }

    /**
     * Récupère un spot par son ID.
     *
     * @param id l'identifiant du spot
     * @return le spot trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> getSpotById(@PathVariable Long id) {
        log.info("Requête GET /api/spots/{} - Récupération du spot", id);

        SpotResponse spot = spotService.getSpotById(id);

        return ResponseEntity.ok(spot);
    }

    /**
     * Récupère les spots par ville.
     *
     * @param city le nom de la ville
     * @return liste des spots dans cette ville
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<SpotResponse>> getSpotsByCity(@PathVariable String city) {
        log.info("Requête GET /api/spots/city/{} - Récupération par ville", city);

        List<SpotResponse> spots = spotService.getSpotsByCity(city);

        return ResponseEntity.ok(spots);
    }

    /**
     * Récupère les spots indoor ou outdoor.
     *
     * @param isIndoor true pour indoor, false pour outdoor
     * @return liste des spots correspondants
     */
    @GetMapping("/type")
    public ResponseEntity<List<SpotResponse>> getSpotsByType(
            @RequestParam(name = "indoor") Boolean isIndoor) {

        log.info("Requête GET /api/spots/type?indoor={} - Récupération par type", isIndoor);

        List<SpotResponse> spots = spotService.getSpotsByType(isIndoor);

        return ResponseEntity.ok(spots);
    }

    /**
     * Récupère les spots à proximité d'une position GPS.
     * Endpoint essentiel pour l'application mobile (affichage carte).
     *
     * @param lat latitude du point de référence
     * @param lng longitude du point de référence
     * @param radius rayon de recherche en kilomètres (défaut: 10 km)
     * @return liste des spots dans le rayon
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<SpotResponse>> getSpotsNearby(
            @RequestParam(name = "lat") BigDecimal lat,
            @RequestParam(name = "lng") BigDecimal lng,
            @RequestParam(name = "radius", defaultValue = "10") double radius) {

        log.info("Requête GET /api/spots/nearby?lat={}&lng={}&radius={}", lat, lng, radius);

        List<SpotResponse> spots = spotService.getSpotsNearby(lat, lng, radius);

        return ResponseEntity.ok(spots);
    }

    /**
     * Met à jour un spot existant.
     *
     * @param id l'identifiant du spot
     * @param requestDTO les nouvelles données
     * @return le spot mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpotResponse> updateSpot(
            @PathVariable Long id,
            @Valid @RequestBody SpotRequest requestDTO) {

        log.info("Requête PUT /api/spots/{} - Mise à jour du spot", id);

        SpotResponse spot = spotService.updateSpot(id, requestDTO);

        return ResponseEntity.ok(spot);
    }

    /**
     * Désactive un spot (soft delete).
     *
     * @param id l'identifiant du spot
     * @return statut 204 NO CONTENT
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateSpot(@PathVariable Long id) {
        log.info("Requête PATCH /api/spots/{}/deactivate - Désactivation", id);

        spotService.deactivateSpot(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime définitivement un spot.
     *
     * @param id l'identifiant du spot
     * @return statut 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        log.info("Requête DELETE /api/spots/{} - Suppression", id);

        spotService.deleteSpot(id);

        return ResponseEntity.noContent().build();
    }
}