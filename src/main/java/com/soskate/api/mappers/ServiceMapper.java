package com.soskate.api.mappers;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.entities.ServiceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre ServiceEntity et ses DTOs.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class ServiceMapper {

    /**
     * Convertit un ServiceRequestDTO en ServiceEntity.
     *
     * @param request le DTO de requête
     * @return l'entité Service
     */
    public ServiceEntity toEntity(ServiceRequest request) {
        if (request == null) {
            return null;
        }

        return ServiceEntity.builder()
                .name(request.name())
                .type(request.type())
                .description(request.description())
                .basePriceCents(request.basePriceCents())
                .isActive(request.isActive())
                .build();
    }

    /**
     * Convertit une ServiceEntity en ServiceResponseDTO.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse
     */
    public ServiceResponse toResponse(ServiceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getDescription(),
                entity.getBasePriceCents(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param serviceToUpdate l'entité à mettre à jour
     * @param request le DTO contenant les nouvelles valeurs
     */
    public void updateEntityFromRequest(ServiceEntity serviceToUpdate, ServiceRequest request) {
        if (serviceToUpdate == null || request == null) {
            return;
        }

        serviceToUpdate.setName(request.name());
        serviceToUpdate.setType(request.type());
        serviceToUpdate.setDescription(request.description());
        serviceToUpdate.setBasePriceCents(request.basePriceCents());
        serviceToUpdate.setIsActive(request.isActive());
    }
}