package com.soskate.api.mappers;

import com.soskate.api.dtos.spot.SpotListDTO;
import com.soskate.api.dtos.spot.SpotRequestDTO;
import com.soskate.api.dtos.spot.SpotResponseDTO;
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
    public static SpotEntity spotRequestDTOtoSpotEntity(SpotRequestDTO dto) {
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
    public static SpotResponseDTO spotEntityToSpotResponseDTO(SpotEntity entity) {
        if (entity == null) {
            return null;
        }

        return new SpotResponseDTO(
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
                entity.getPhotos(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convertit une SpotEntity en SpotListDTO (version simplifiée pour la carte).
     *
     * @param entity l'entité à convertir
     * @return le DTO simplifié pour la liste/carte
     */
    public static SpotListDTO spotEntityToSpotListDTO(SpotEntity entity) {
        if (entity == null) {
            return null;
        }

        int eventCount = entity.getEvents() != null ?
                (int) entity.getEvents().stream()
                        .filter(event -> event.getStartTime().isAfter(java.time.LocalDateTime.now()))
                        .count() : 0;

        return new SpotListDTO(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getIsIndoor(),
                eventCount
        );
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param entity l'entité à mettre à jour
     * @param dto le DTO contenant les nouvelles valeurs
     */
    public static void updateEntityFromDTO(SpotEntity entity, SpotRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setAddress(dto.address());
        entity.setCity(dto.city());
        entity.setZipCode(dto.zipCode());
        entity.setLatitude(dto.latitude());
        entity.setLongitude(dto.longitude());
        entity.setIsIndoor(dto.isIndoor());
        entity.setIsActive(dto.isActive());
    }
}