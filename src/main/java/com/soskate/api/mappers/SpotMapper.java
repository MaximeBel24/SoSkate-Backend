package com.soskate.api.mappers;

import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;
import com.soskate.api.entities.SpotEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre SpotEntity et ses DTOs.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class SpotMapper {

    /**
     * Convertit un SpotRequestDTO en SpotEntity.
     *
     * @param request le DTO de requête
     * @return l'entité Spot
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
     * Convertit une SpotEntity en SpotResponseDTO.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse complet
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
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param entity l'entité à mettre à jour
     * @param request le DTO contenant les nouvelles valeurs
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