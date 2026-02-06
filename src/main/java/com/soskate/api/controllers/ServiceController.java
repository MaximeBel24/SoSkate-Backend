package com.soskate.api.controllers;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.enums.ServiceType;
import com.soskate.api.services.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des services/cours de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Tag(name = "Services", description = "Gestion des services et cours de skateboard")
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(
            summary = "Créer un service",
            description = "Crée un nouveau service/cours"
    )
    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody ServiceRequest requestDTO) {

        log.info("Requête POST /api/services - Création d'un service : {}", requestDTO.name());

        ServiceResponse service = serviceService.createService(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @Operation(
            summary = "Lister tous les services",
            description = "Récupère tous les services"
    )
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        log.info("Requête GET /api/services - Récupération de tous les services");

        List<ServiceResponse> services = serviceService.getAllServices();

        log.info("{} service(s) trouvé(s)", services.size());

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Lister les services actifs",
            description = "Récupère uniquement les services actifs"
    )
    @GetMapping("/active")
    public ResponseEntity<List<ServiceResponse>> getActiveServices() {
        log.info("Requête GET /api/services/active - Récupération des services actifs");

        List<ServiceResponse> services = serviceService.getActiveServices();

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Récupérer un service",
            description = "Récupère un service par son ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        log.info("Requête GET /api/services/{} - Récupération du service", id);

        ServiceResponse service = serviceService.getServiceById(id);

        return ResponseEntity.ok(service);
    }

    @Operation(
            summary = "Filtrer par type",
            description = "Récupère les services par type (LESSON, RENTAL)"
    )
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ServiceResponse>> getServicesByType(@PathVariable ServiceType type) {
        log.info("Requête GET /api/services/type/{} - Récupération par type", type);

        List<ServiceResponse> services = serviceService.getServicesByType(type);

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Modifier un service",
            description = "Met à jour un service existant"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest requestDTO) {

        log.info("Requête PUT /api/services/{} - Mise à jour du service", id);

        ServiceResponse service = serviceService.updateService(id, requestDTO);

        return ResponseEntity.ok(service);
    }

    @Operation(
            summary = "Désactiver un service",
            description = "Désactive un service (soft delete)"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateService(@PathVariable Long id) {
        log.info("Requête PATCH /api/services/{}/deactivate - Désactivation", id);

        serviceService.deactivateService(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Supprimer un service",
            description = "Supprime définitivement un service"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.info("Requête DELETE /api/services/{} - Suppression", id);

        serviceService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}