package com.soskate.api.service;

import com.soskate.api.dto.photo.PhotoListResponse;
import com.soskate.api.dto.photo.PhotoResponse;
import com.soskate.api.dto.photo.PhotoUploadRequest;
import com.soskate.api.dto.photo.PhotoUpdateRequest;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service for photo management.
 * Handles upload, conversion, validation, and CRUD operations.
 */
public interface PhotoService {

    /**
     * Upload a new photo.
     * - Validates file format and size
     * - Converts to WebP
     * - Generates thumbnail
     * - Uploads to S3
     * - Saves metadata to database
     *
     * @param request Upload request with file and metadata
     * @return PhotoResponse with uploaded photo details
     */
    PhotoResponse uploadPhoto(PhotoUploadRequest request);

    /**
     * Get a photo by ID.
     *
     * @param photoId Photo ID
     * @return PhotoResponse
     * @throws com.soskate.api.exceptions.photo.PhotoNotFoundException if not found
     */
    PhotoResponse getPhotoById(Long photoId);

    /**
     * Get all photos for a specific entity.
     *
     * @param entityType Entity type (SPOT, CUSTOMER, etc.)
     * @param entityId   Entity ID
     * @return List of photos ordered by displayOrder
     */
    List<PhotoResponse> getPhotosByEntity(PhotoEntityType entityType, Long entityId);

    /**
     * Get photos by entity and photo type.
     *
     * @param entityType Entity type
     * @param entityId   Entity ID
     * @param photoType  Photo type (AVATAR, GALLERY, etc.)
     * @return List of photos
     */
    List<PhotoResponse> getPhotosByEntityAndType(PhotoEntityType entityType, Long entityId, PhotoType photoType);

    /**
     * Get avatar photo for an entity (Customer or Instructor).
     *
     * @param entityType Entity type
     * @param entityId   Entity ID
     * @return PhotoResponse or null if no avatar
     */
    PhotoResponse getAvatarByEntity(PhotoEntityType entityType, Long entityId);

    /**
     * Update photo metadata (display order).
     *
     * @param photoId Photo ID
     * @param request Update request
     * @return Updated PhotoResponse
     */
    PhotoResponse updatePhoto(Long photoId, PhotoUpdateRequest request);

    /**
     * Delete a photo (soft delete).
     * Also deletes files from S3 asynchronously.
     *
     * @param photoId     Photo ID
     * @param deletedBy   User ID who deleted the photo
     */
    void deletePhoto(Long photoId, Long deletedBy);

    /**
     * Get all photos with pagination (admin view).
     *
     * @param pageable Pagination parameters
     * @return PhotoListResponse with pagination metadata
     */
    PhotoListResponse getAllPhotos(Pageable pageable);

    /**
     * Get photos uploaded by a specific user.
     *
     * @param userId User ID
     * @return List of photos
     */
    List<PhotoResponse> getPhotosByUploader(Long userId);

    /**
     * Clean up old deleted photos from S3.
     * Should be called by a scheduled batch job.
     *
     * @param daysOld Delete photos marked as deleted more than X days ago
     * @return Number of photos cleaned
     */
    int cleanupDeletedPhotos(int daysOld);
}