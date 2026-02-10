package com.soskate.api.mappers;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.entities.ServiceEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ServiceEntity and its DTOs.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class ServiceMapper {

    /**
     * Converts a ServiceRequestDTO to a ServiceEntity.
     *
     * @param request the request DTO
     * @return the Service entity
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
     * Converts a ServiceEntity to a ServiceResponseDTO.
     *
     * @param entity the entity to convert
     * @return the response DTO
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
     * Updates an existing entity with data from a DTO.
     *
     * @param serviceToUpdate the entity to update
     * @param request the DTO containing the new values
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