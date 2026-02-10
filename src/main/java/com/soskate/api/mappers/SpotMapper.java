package com.soskate.api.mappers;

import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;
import com.soskate.api.entities.SpotEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between SpotEntity and its DTOs.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class SpotMapper {

    /**
     * Converts a SpotRequestDTO to a SpotEntity.
     *
     * @param request the request DTO
     * @return the Spot entity
     */
    public SpotEntity toEntity(SpotRequest request) {
        if (request == null) {
            return null;
        }

        return SpotEntity.builder()
                .name(request.name())
                .description(request.description())
                .address(request.address())
                .city(request.city())
                .zipCode(request.zipCode())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .isIndoor(request.isIndoor())
                .isActive(request.isActive())
                .build();
    }

    /**
     * Converts a SpotEntity to a SpotResponseDTO.
     *
     * @param entity the entity to convert
     * @return the full response DTO
     */
    public SpotResponse toResponse(SpotEntity entity) {
        if (entity == null) {
            return null;
        }

        return new SpotResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getAddress(),
                entity.getCity(),
                entity.getZipCode(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getIsIndoor(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Updates an existing entity with data from a DTO.
     *
     * @param entity the entity to update
     * @param request the DTO containing the new values
     */
    public void updateEntityFromRequest(SpotEntity entity, SpotRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setAddress(request.address());
        entity.setCity(request.city());
        entity.setZipCode(request.zipCode());
        entity.setLatitude(request.latitude());
        entity.setLongitude(request.longitude());
        entity.setIsIndoor(request.isIndoor());
        entity.setIsActive(request.isActive());
    }
}