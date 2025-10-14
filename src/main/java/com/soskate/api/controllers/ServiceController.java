package com.soskate.api.controllers;

import com.soskate.api.dtos.service.ServiceResponseDto;
import com.soskate.api.dtos.service.ServiceRequestDto;
import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.services.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des services
 *
 * Endpoints disponibles :
 * - GET /services : Récupérer tous les services
 * - GET /services/{id} : Récupérer un service par ID
 * - POST /services : Créer un nouveau service
 * - PUT /services/{id} : Mettre à jour un service existant
 * - DELETE /services/{id} : Supprimer un service
 */
@RestController
@RequestMapping("/services")
@Validated
@Tag(name = "Services", description = "API de gestion des services SoSkate")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * Récupère tous les services disponibles
     *
     * @return Liste de tous les services
     */
    @Operation(
            summary = "Récupérer tous les services",
            description = "Retourne la liste complète de tous les services disponibles dans le système"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des services récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public List<ServiceResponseDto> getAllServices() {
        log.info("Request to get all services");
        List<ServiceResponseDto> services = serviceService.getAllServices();
        log.info("Retrieved {} services", services.size());
        return services;
    }

    /**
     * Récupère un service spécifique par son ID
     *
     * @param id L'identifiant du service à récupérer
     * @return Le service correspondant à l'ID
     */
    @Operation(
            summary = "Récupérer un service par ID",
            description = "Retourne les détails d'un service spécifique basé sur son identifiant"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service trouvé et retourné avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID invalide fourni",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ServiceResponseDto getServiceById(
            @Parameter(description = "ID du service à récupérer", required = true, example = "1")
            @PathVariable("id")
            @Min(value = 1, message = "L'ID du service doit être un nombre positif")
            Long id) {

        log.info("Request to get service with id: {}", id);
        ServiceResponseDto service = serviceService.getServiceById(id);
        log.info("Retrieved service: {} (type: {})", service.name(), service.type());
        return service;
    }

    /**
     * Crée un nouveau service
     *
     * @param serviceToCreate Les données du service à créer
     * @return Le service créé avec son ID généré
     */
    @Operation(
            summary = "Créer un nouveau service",
            description = "Crée un nouveau service avec les données fournies. Le nom et le type doivent être uniques."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Service créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de validation invalides",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service avec ce nom et type existe déjà",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponseDto createService(
            @Parameter(description = "Données du service à créer", required = true)
            @Valid @RequestBody ServiceRequestDto serviceToCreate) {

        log.info("Request to create service: {} (type: {})", serviceToCreate.name(), serviceToCreate.type());
        ServiceResponseDto createdService = serviceService.createService(serviceToCreate);
        log.info("Service created successfully with id: {}", createdService.id());
        return createdService;
    }

    /**
     * Met à jour un service existant
     *
     * @param id L'identifiant du service à mettre à jour
     * @param serviceToUpdate Les nouvelles données du service
     * @return Le service mis à jour
     */
    @Operation(
            summary = "Mettre à jour un service existant",
            description = "Met à jour toutes les données d'un service existant. Le nom et le type doivent rester uniques."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServiceResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID invalide ou données de validation invalides",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service à mettre à jour non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Un autre service avec ce nom et type existe déjà",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ServiceResponseDto updateService(
            @Parameter(description = "ID du service à mettre à jour", required = true, example = "1")
            @PathVariable("id")
            @Min(value = 1, message = "L'ID du service doit être un nombre positif")
            Long id,

            @Parameter(description = "Nouvelles données du service", required = true)
            @Valid @RequestBody ServiceRequestDto serviceToUpdate) {

        log.info("Request to update service with id: {} - New data: {} (type: {})",
                id, serviceToUpdate.name(), serviceToUpdate.type());
        ServiceResponseDto updatedService = serviceService.updateService(id, serviceToUpdate);
        log.info("Service updated successfully: {}", updatedService.id());
        return updatedService;
    }

    /**
     * Supprime un service existant
     *
     * @param id L'identifiant du service à supprimer
     */
    @Operation(
            summary = "Supprimer un service",
            description = "Supprime définitivement un service du système. Cette action est irréversible."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Service supprimé avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID invalide fourni",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service à supprimer non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(
            @Parameter(description = "ID du service à supprimer", required = true, example = "1")
            @PathVariable("id")
            @Min(value = 1, message = "L'ID du service doit être un nombre positif")
            Long id) {

        log.info("Request to delete service with id: {}", id);
        serviceService.deleteService(id);
        log.info("Service deleted successfully: {}", id);
    }
}