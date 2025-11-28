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
     * @param dto le DTO de requête
     * @return l'entité Spot
     */
    public static SpotEntity spotRequestDTOtoSpotEntity(SpotRequest dto) {
        if (dto == null) {
            return null;
        }

        return new SpotEntity(
                dto.name(),
                dto.description(),
                dto.address(),
                dto.city(),
                dto.zipCode(),
                dto.latitude(),
                dto.longitude(),
                dto.isIndoor(),
                dto.isActive()
        );
    }

    /**
     * Convertit une SpotEntity en SpotResponseDTO.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse complet
     */
    public static SpotResponse spotEntityToSpotResponseDTO(SpotEntity entity) {
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
     * @param spotEntity l'entité à mettre à jour
     * @param spotRequest le DTO contenant les nouvelles valeurs
     */
    public static void updateSpotEntityFromSpotRequestDTO(SpotEntity spotEntity, SpotRequest spotRequest) {
        if (spotEntity == null || spotRequest == null) {
            return;
        }

        spotEntity.setName(spotRequest.name());
        spotEntity.setDescription(spotRequest.description());
        spotEntity.setAddress(spotRequest.address());
        spotEntity.setCity(spotRequest.city());
        spotEntity.setZipCode(spotRequest.zipCode());
        spotEntity.setLatitude(spotRequest.latitude());
        spotEntity.setLongitude(spotRequest.longitude());
        spotEntity.setIsIndoor(spotRequest.isIndoor());
        spotEntity.setIsActive(spotRequest.isActive());
    }
}