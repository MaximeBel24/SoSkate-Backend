package com.soskate.api.mappers;

import com.soskate.api.dto.photo.PhotoListResponse;
import com.soskate.api.dto.photo.PhotoResponse;
import com.soskate.api.entities.PhotoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between PhotoEntity and PhotoResponse DTO.
 */
@Component
public class PhotoMapper {

    /**
     * Convert PhotoEntity to PhotoResponse DTO.
     */
    public PhotoResponse toResponse(PhotoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PhotoResponse(
                entity.getId(),
                entity.getUrl(),
                entity.getThumbnailUrl(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getPhotoType(),
                entity.getOriginalFileName(),
                entity.getFileSize(),
                entity.getMimeType(),
                entity.getWidth(),
                entity.getHeight(),
                entity.getDisplayOrder(),
                entity.getUploadedBy(),
                entity.getUploadedAt()
        );
    }

    /**
     * Convert list of PhotoEntity to list of PhotoResponse.
     */
    public List<PhotoResponse> toResponseList(List<PhotoEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create PhotoListResponse with pagination metadata.
     */
    public PhotoListResponse toListResponse(List<PhotoEntity> entities, int totalCount, int page, int pageSize) {
        List<PhotoResponse> photoResponses = toResponseList(entities);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return PhotoListResponse.builder()
                .photos(photoResponses)
                .totalCount(totalCount)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
