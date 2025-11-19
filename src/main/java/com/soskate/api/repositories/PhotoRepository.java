package com.soskate.api.repositories;

import com.soskate.api.entities.PhotoEntity;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PhotoEntity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, Long> {

    /**
     * Find all photos for a specific entity (e.g., all photos of a Spot).
     * Excludes soft-deleted photos.
     */
    List<PhotoEntity> findByEntityTypeAndEntityIdAndDeletedFalseOrderByDisplayOrderAsc(
            PhotoEntityType entityType,
            Long entityId
    );

    /**
     * Find photos by entity type, entity ID, and photo type.
     * Useful for finding specific photo types (e.g., AVATAR only).
     */
    List<PhotoEntity> findByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
            PhotoEntityType entityType,
            Long entityId,
            PhotoType photoType
    );

    /**
     * Find a single photo by entity, ignoring photo type.
     * Used for avatars (should return 0 or 1 result).
     */
    Optional<PhotoEntity> findFirstByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
            PhotoEntityType entityType,
            Long entityId,
            PhotoType photoType
    );

    /**
     * Count photos for a specific entity and photo type.
     * Used to enforce photo limits.
     */
    int countByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
            PhotoEntityType entityType,
            Long entityId,
            PhotoType photoType
    );

    /**
     * Find all photos with pagination (admin view).
     */
    Page<PhotoEntity> findByDeletedFalse(Pageable pageable);

    /**
     * Find photos uploaded by a specific user.
     */
    List<PhotoEntity> findByUploadedByAndDeletedFalseOrderByUploadedAtDesc(Long userId);

    /**
     * Find soft-deleted photos older than a certain date (for cleanup batch job).
     */
    @Query("SELECT p FROM PhotoEntity p WHERE p.deleted = true AND p.deletedAt < :cutoffDate")
    List<PhotoEntity> findDeletedPhotosOlderThan(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);

    /**
     * Check if a photo exists for a specific entity.
     */
    boolean existsByEntityTypeAndEntityIdAndDeletedFalse(PhotoEntityType entityType, Long entityId);
}