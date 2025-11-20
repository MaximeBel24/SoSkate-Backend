package com.soskate.api.controllers;

import com.soskate.api.dto.photo.PhotoListResponse;
import com.soskate.api.dto.photo.PhotoResponse;
import com.soskate.api.dto.photo.PhotoUpdateRequest;
import com.soskate.api.dto.photo.PhotoUploadRequest;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import com.soskate.api.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for photo management.
 * Handles photo upload, retrieval, update, and deletion.
 */
@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {

    private final PhotoService photoService;

    // ========== UPLOAD PHOTO ==========

    /**
     * Upload a new photo.
     *
     * POST /api/photos
     * Content-Type: multipart/form-data
     *
     * @param file File to upload
     * @param entityType Entity type (SPOT, CUSTOMER, INSTRUCTOR, EVENT)
     * @param entityId Entity ID
     * @param photoType Photo type (AVATAR, COVER, GALLERY, TRICK)
     * @param displayOrder Display order (optional, default 0)
     * @return Uploaded photo details
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") PhotoEntityType entityType,
            @RequestParam("entityId") Long entityId,
            @RequestParam("photoType") PhotoType photoType,
            @RequestParam(value = "displayOrder", required = false, defaultValue = "0") Integer displayOrder,
            @RequestParam(value = "uploadedBy", required = false) Long uploadedBy
    ) {
        log.info("POST /api/photos - Upload photo: entityType={}, entityId={}, photoType={}",
                entityType, entityId, photoType);

        PhotoUploadRequest request = PhotoUploadRequest.builder()
                .file(file)
                .entityType(entityType)
                .entityId(entityId)
                .photoType(photoType)
                .displayOrder(displayOrder)
                .uploadedBy(uploadedBy) // TODO: Get from authentication context
                .build();

        PhotoResponse response = photoService.uploadPhoto(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========== GET PHOTO BY ID ==========

    /**
     * Get a photo by ID.
     *
     * GET /api/photos/{id}
     *
     * @param id Photo ID
     * @return Photo details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhotoResponse> getPhotoById(@PathVariable Long id) {
        log.info("GET /api/photos/{}", id);

        PhotoResponse response = photoService.getPhotoById(id);
        return ResponseEntity.ok(response);
    }

    // ========== GET PHOTOS BY ENTITY ==========

    /**
     * Get all photos for a specific entity.
     *
     * GET /api/photos?entityType=SPOT&entityId=123
     *
     * @param entityType Entity type
     * @param entityId Entity ID
     * @param photoType Photo type (optional filter)
     * @return List of photos
     */
    @GetMapping
    public ResponseEntity<List<PhotoResponse>> getPhotosByEntity(
            @RequestParam PhotoEntityType entityType,
            @RequestParam Long entityId,
            @RequestParam(required = false) PhotoType photoType
    ) {
        log.info("GET /api/photos?entityType={}&entityId={}&photoType={}", entityType, entityId, photoType);

        List<PhotoResponse> photos;

        if (photoType != null) {
            photos = photoService.getPhotosByEntityAndType(entityType, entityId, photoType);
        } else {
            photos = photoService.getPhotosByEntity(entityType, entityId);
        }

        return ResponseEntity.ok(photos);
    }

    // ========== CONVENIENCE ENDPOINTS FOR SPECIFIC ENTITIES ==========

    /**
     * Get all photos for a Spot.
     *
     * GET /api/photos/spots/{spotId}
     */
    @GetMapping("/spots/{spotId}")
    public ResponseEntity<List<PhotoResponse>> getSpotPhotos(@PathVariable Long spotId) {
        log.info("GET /api/photos/spots/{}", spotId);

        List<PhotoResponse> photos = photoService.getPhotosByEntity(PhotoEntityType.SPOT, spotId);
        return ResponseEntity.ok(photos);
    }

    /**
     * Get avatar for a Customer.
     *
     * GET /api/photos/customers/{customerId}/avatar
     */
    @GetMapping("/customers/{customerId}/avatar")
    public ResponseEntity<PhotoResponse> getCustomerAvatar(@PathVariable Long customerId) {
        log.info("GET /api/photos/customers/{}/avatar", customerId);

        PhotoResponse avatar = photoService.getAvatarByEntity(PhotoEntityType.CUSTOMER, customerId);

        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(avatar);
    }

    /**
     * Get avatar for an Instructor.
     *
     * GET /api/photos/instructors/{instructorId}/avatar
     */
    @GetMapping("/instructors/{instructorId}/avatar")
    public ResponseEntity<PhotoResponse> getInstructorAvatar(@PathVariable Long instructorId) {
        log.info("GET /api/photos/instructors/{}/avatar", instructorId);

        PhotoResponse avatar = photoService.getAvatarByEntity(PhotoEntityType.INSTRUCTOR, instructorId);

        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(avatar);
    }

    /**
     * Get trick/course photos for an Instructor.
     *
     * GET /api/photos/instructors/{instructorId}/tricks
     */
    @GetMapping("/instructors/{instructorId}/tricks")
    public ResponseEntity<List<PhotoResponse>> getInstructorTricks(@PathVariable Long instructorId) {
        log.info("GET /api/photos/instructors/{}/tricks", instructorId);

        List<PhotoResponse> tricks = photoService.getPhotosByEntityAndType(
                PhotoEntityType.INSTRUCTOR, instructorId, PhotoType.TRICK
        );

        return ResponseEntity.ok(tricks);
    }

    /**
     * Get cover photo for an Event.
     *
     * GET /api/photos/events/{eventId}/cover
     */
    @GetMapping("/events/{eventId}/cover")
    public ResponseEntity<PhotoResponse> getEventCover(@PathVariable Long eventId) {
        log.info("GET /api/photos/events/{}/cover", eventId);

        PhotoResponse cover = photoService.getAvatarByEntity(PhotoEntityType.EVENT, eventId);

        if (cover == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cover);
    }

    /**
     * Get gallery photos for an Event.
     *
     * GET /api/photos/events/{eventId}/gallery
     */
    @GetMapping("/events/{eventId}/gallery")
    public ResponseEntity<List<PhotoResponse>> getEventGallery(@PathVariable Long eventId) {
        log.info("GET /api/photos/events/{}/gallery", eventId);

        List<PhotoResponse> gallery = photoService.getPhotosByEntityAndType(
                PhotoEntityType.EVENT, eventId, PhotoType.GALLERY
        );

        return ResponseEntity.ok(gallery);
    }

    // ========== UPDATE PHOTO ==========

    /**
     * Update photo metadata (display order).
     *
     * PATCH /api/photos/{id}
     *
     * @param id Photo ID
     * @param request Update request
     * @return Updated photo details
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PhotoResponse> updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody PhotoUpdateRequest request
    ) {
        log.info("PATCH /api/photos/{} - Update photo", id);

        PhotoResponse response = photoService.updatePhoto(id, request);
        return ResponseEntity.ok(response);
    }

    // ========== DELETE PHOTO ==========

    /**
     * Delete a photo (soft delete).
     *
     * DELETE /api/photos/{id}
     *
     * @param id Photo ID
     * @param deletedBy User ID who deletes (optional, from auth context)
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long id,
            @RequestParam(value = "deletedBy", required = false) Long deletedBy
    ) {
        log.info("DELETE /api/photos/{}", id);

        // TODO: Get deletedBy from authentication context
        photoService.deletePhoto(id, deletedBy);

        return ResponseEntity.noContent().build();
    }

    // ========== ADMIN ENDPOINTS ==========

    /**
     * Get all photos with pagination (admin).
     *
     * GET /api/photos/all?page=0&size=20&sort=uploadedAt,desc
     *
     * @param page Page number (default 0)
     * @param size Page size (default 20)
     * @param sort Sort field (default uploadedAt,desc)
     * @return Paginated photo list
     */
    @GetMapping("/all")
    public ResponseEntity<PhotoListResponse> getAllPhotos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "uploadedAt,desc") String[] sort
    ) {
        log.info("GET /api/photos/all?page={}&size={}", page, size);

        // Parse sort parameter
        Sort.Direction direction = Sort.Direction.DESC;
        String sortField = "uploadedAt";

        if (sort.length > 0) {
            sortField = sort[0];
            if (sort.length > 1) {
                direction = Sort.Direction.fromString(sort[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        PhotoListResponse response = photoService.getAllPhotos(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * Get photos uploaded by a specific user.
     *
     * GET /api/photos/uploader/{userId}
     *
     * @param userId User ID
     * @return List of photos
     */
    @GetMapping("/uploader/{userId}")
    public ResponseEntity<List<PhotoResponse>> getPhotosByUploader(@PathVariable Long userId) {
        log.info("GET /api/photos/uploader/{}", userId);

        List<PhotoResponse> photos = photoService.getPhotosByUploader(userId);
        return ResponseEntity.ok(photos);
    }

    /**
     * Cleanup old deleted photos (admin/batch job).
     *
     * POST /api/photos/cleanup?daysOld=7
     *
     * @param daysOld Number of days since deletion
     * @return Number of photos cleaned
     */
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupDeletedPhotos(
            @RequestParam(defaultValue = "7") int daysOld
    ) {
        log.info("POST /api/photos/cleanup?daysOld={}", daysOld);

        int cleanedCount = photoService.cleanupDeletedPhotos(daysOld);

        return ResponseEntity.ok(String.format("Nettoyage effectué : %d photos supprimées", cleanedCount));
    }
}