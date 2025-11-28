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
     * @param serviceRequestDTO le DTO de requête
     * @return l'entité Service
     */
    public static ServiceEntity serviceRequestDTOToServiceEntity(ServiceRequest serviceRequestDTO) {
        if (serviceRequestDTO == null) {
            return null;
        }

        return ServiceEntity.builder()
                .name(serviceRequestDTO.name())
                .type(serviceRequestDTO.type())
                .description(serviceRequestDTO.description())
                .durationMinutes(serviceRequestDTO.durationMinutes())
                .basePriceCents(serviceRequestDTO.basePriceCents())
                .isActive(serviceRequestDTO.isActive())
                .build();
    }

    /**
     * Convertit une ServiceEntity en ServiceResponseDTO.
     *
     * @param serviceEntity l'entité à convertir
     * @return le DTO de réponse
     */
    public static ServiceResponse serviceEntityToServiceResponseDTO(ServiceEntity serviceEntity) {
        if (serviceEntity == null) {
            return null;
        }

        return new ServiceResponse(
                serviceEntity.getId(),
                serviceEntity.getName(),
                serviceEntity.getType(),
                serviceEntity.getDescription(),
                serviceEntity.getDurationMinutes(),
                serviceEntity.getBasePriceCents(),
                serviceEntity.getIsActive(),
                serviceEntity.getCreatedAt(),
                serviceEntity.getUpdatedAt()
        );
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param serviceToUpdate l'entité à mettre à jour
     * @param serviceRequestDTO le DTO contenant les nouvelles valeurs
     */
    public static void updateEntityFromDTO(ServiceEntity serviceToUpdate, ServiceRequest serviceRequestDTO) {
        if (serviceToUpdate == null || serviceRequestDTO == null) {
            return;
        }

        serviceToUpdate.setName(serviceRequestDTO.name());
        serviceToUpdate.setType(serviceRequestDTO.type());
        serviceToUpdate.setDescription(serviceRequestDTO.description());
        serviceToUpdate.setDurationMinutes(serviceRequestDTO.durationMinutes());
        serviceToUpdate.setBasePriceCents(serviceRequestDTO.basePriceCents());
        serviceToUpdate.setIsActive(serviceRequestDTO.isActive());
    }
}