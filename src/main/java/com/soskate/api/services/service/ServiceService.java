package com.soskate.api.services.service;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.enums.ServiceType;

import java.util.List;

/**
 * Service pour la gestion des services/cours de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface ServiceService {

    /**
     * Crée un nouveau service.
     *
     * @param requestDTO les données du service à créer
     * @return le service créé
     */
    ServiceResponse createService(ServiceRequest requestDTO);

    /**
     * Récupère tous les services.
     *
     * @return liste de tous les services
     */
    List<ServiceResponse> getAllServices();

    /**
     * Récupère uniquement les services actifs.
     *
     * @return liste des services actifs
     */
    List<ServiceResponse> getActiveServices();

    /**
     * Récupère un service par son ID.
     *
     * @param id l'identifiant du service
     * @return le service trouvé
     */
    ServiceResponse getServiceById(Long id);

    /**
     * Récupère les services par type.
     *
     * @param type le type de service (LESSON, RENTAL)
     * @return liste des services de ce type
     */
    List<ServiceResponse> getServicesByType(ServiceType type);

    /**
     * Met à jour un service existant.
     *
     * @param id l'identifiant du service
     * @param requestDTO les nouvelles données
     * @return le service mis à jour
     */
    ServiceResponse updateService(Long id, ServiceRequest requestDTO);

    /**
     * Désactive un service (soft delete).
     *
     * @param id l'identifiant du service
     */
    void deactivateService(Long id);

    /**
     * Supprime définitivement un service.
     *
     * @param id l'identifiant du service
     */
    void deleteService(Long id);
}