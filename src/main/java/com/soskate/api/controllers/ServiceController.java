package com.soskate.api.controllers;

import com.soskate.api.dtos.service.ServiceRequestDTO;
import com.soskate.api.dtos.service.ServiceResponseDTO;
import com.soskate.api.enums.ServiceType;
import com.soskate.api.services.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des services/cours de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Crée un nouveau service.
     *
     * @param requestDTO les données du service
     * @return le service créé avec statut 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(
            @Valid @RequestBody ServiceRequestDTO requestDTO) {

        log.info("Requête POST /api/services - Création d'un service : {}", requestDTO.name());

        ServiceResponseDTO service = serviceService.createService(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    /**
     * Récupère tous les services.
     *
     * @return liste de tous les services
     */
    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> getAllServices() {
        log.info("Requête GET /api/services - Récupération de tous les services");

        List<ServiceResponseDTO> services = serviceService.getAllServices();

        log.info("{} service(s) trouvé(s)", services.size());

        return ResponseEntity.ok(services);
    }

    /**
     * Récupère uniquement les services actifs.
     *
     * @return liste des services actifs
     */
    @GetMapping("/active")
    public ResponseEntity<List<ServiceResponseDTO>> getActiveServices() {
        log.info("Requête GET /api/services/active - Récupération des services actifs");

        List<ServiceResponseDTO> services = serviceService.getActiveServices();

        return ResponseEntity.ok(services);
    }

    /**
     * Récupère un service par son ID.
     *
     * @param id l'identifiant du service
     * @return le service trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        log.info("Requête GET /api/services/{} - Récupération du service", id);

        ServiceResponseDTO service = serviceService.getServiceById(id);

        return ResponseEntity.ok(service);
    }

    /**
     * Récupère les services par type.
     *
     * @param type le type de service (LESSON, RENTAL)
     * @return liste des services de ce type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ServiceResponseDTO>> getServicesByType(@PathVariable ServiceType type) {
        log.info("Requête GET /api/services/type/{} - Récupération par type", type);

        List<ServiceResponseDTO> services = serviceService.getServicesByType(type);

        return ResponseEntity.ok(services);
    }

    /**
     * Met à jour un service existant.
     *
     * @param id l'identifiant du service
     * @param requestDTO les nouvelles données
     * @return le service mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO requestDTO) {

        log.info("Requête PUT /api/services/{} - Mise à jour du service", id);

        ServiceResponseDTO service = serviceService.updateService(id, requestDTO);

        return ResponseEntity.ok(service);
    }

    /**
     * Désactive un service (soft delete).
     *
     * @param id l'identifiant du service
     * @return statut 204 NO CONTENT
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateService(@PathVariable Long id) {
        log.info("Requête PATCH /api/services/{}/deactivate - Désactivation", id);

        serviceService.deactivateService(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime définitivement un service.
     *
     * @param id l'identifiant du service
     * @return statut 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.info("Requête DELETE /api/services/{} - Suppression", id);

        serviceService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}