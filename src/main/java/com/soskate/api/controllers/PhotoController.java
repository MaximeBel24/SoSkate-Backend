package com.soskate.api.controllers;

import com.soskate.api.dto.photo.PhotoListResponse;
import com.soskate.api.dto.photo.PhotoResponse;
import com.soskate.api.dto.photo.PhotoUpdateRequest;
import com.soskate.api.dto.photo.PhotoUploadRequest;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import com.soskate.api.services.photo.PhotoService;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for photo management.
 * Handles photo upload, retrieval, update, and deletion.
 */
@Tag(name = "Photos", description = "Photo upload and management")
@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@Slf4j
public class PhotoController {

    private final PhotoService photoService;

    // ========== UPLOAD PHOTO ==========

    @Operation(
            summary = "Upload a photo",
            description = "Uploads a new photo (multipart/form-data)"
    )
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

    @Operation(
            summary = "Retrieve a photo",
            description = "Retrieves a photo by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PhotoResponse> getPhotoById(@PathVariable Long id) {
        log.info("GET /api/photos/{}", id);

        PhotoResponse response = photoService.getPhotoById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Photos of an entity",
            description = "Retrieves the photos of an entity (spot, customer, instructor, event)"
    )
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

    @Operation(
            summary = "Photos of a spot",
            description = "Retrieves all photos of a spot"
    )
    @GetMapping("/spots/{spotId}")
    public ResponseEntity<List<PhotoResponse>> getSpotPhotos(@PathVariable Long spotId) {
        log.info("GET /api/photos/spots/{}", spotId);

        List<PhotoResponse> photos = photoService.getPhotosByEntity(PhotoEntityType.SPOT, spotId);
        return ResponseEntity.ok(photos);
    }

    @Operation(
            summary = "Customer avatar",
            description = "Retrieves a customer's avatar"
    )
    @GetMapping("/customers/{customerId}/avatar")
    public ResponseEntity<PhotoResponse> getCustomerAvatar(@PathVariable Long customerId) {
        log.info("GET /api/photos/customers/{}/avatar", customerId);

        PhotoResponse avatar = photoService.getAvatarByEntity(PhotoEntityType.CUSTOMER, customerId);

        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(avatar);
    }

    @Operation(
            summary = "Instructor avatar",
            description = "Retrieves an instructor's avatar"
    )
    @GetMapping("/instructors/{instructorId}/avatar")
    public ResponseEntity<PhotoResponse> getInstructorAvatar(@PathVariable Long instructorId) {
        log.info("GET /api/photos/instructors/{}/avatar", instructorId);

        PhotoResponse avatar = photoService.getAvatarByEntity(PhotoEntityType.INSTRUCTOR, instructorId);

        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(avatar);
    }

    @Operation(
            summary = "Trick photos",
            description = "Retrieves an instructor's trick photos"
    )
    @GetMapping("/instructors/{instructorId}/tricks")
    public ResponseEntity<List<PhotoResponse>> getInstructorTricks(@PathVariable Long instructorId) {
        log.info("GET /api/photos/instructors/{}/tricks", instructorId);

        List<PhotoResponse> tricks = photoService.getPhotosByEntityAndType(
                PhotoEntityType.INSTRUCTOR, instructorId, PhotoType.TRICK
        );

        return ResponseEntity.ok(tricks);
    }

    @Operation(
            summary = "Event cover",
            description = "Retrieves the cover photo of an event"
    )
    @GetMapping("/events/{eventId}/cover")
    public ResponseEntity<PhotoResponse> getEventCover(@PathVariable Long eventId) {
        log.info("GET /api/photos/events/{}/cover", eventId);

        PhotoResponse cover = photoService.getAvatarByEntity(PhotoEntityType.EVENT, eventId);

        if (cover == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cover);
    }

    @Operation(
            summary = "Event gallery",
            description = "Retrieves the gallery photos of an event"
    )
    @GetMapping("/events/{eventId}/gallery")
    public ResponseEntity<List<PhotoResponse>> getEventGallery(@PathVariable Long eventId) {
        log.info("GET /api/photos/events/{}/gallery", eventId);

        List<PhotoResponse> gallery = photoService.getPhotosByEntityAndType(
                PhotoEntityType.EVENT, eventId, PhotoType.GALLERY
        );

        return ResponseEntity.ok(gallery);
    }

    @Operation(
            summary = "Update a photo",
            description = "Updates a photo's metadata"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<PhotoResponse> updatePhoto(
            @PathVariable Long id,
            @Valid @RequestBody PhotoUpdateRequest request
    ) {
        log.info("PATCH /api/photos/{} - Update photo", id);

        PhotoResponse response = photoService.updatePhoto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a photo",
            description = "Deletes a photo (soft delete)"
    )
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

    @Operation(
            summary = "List all photos (admin)",
            description = "Retrieves all photos with pagination"
    )
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

    @Operation(
            summary = "Photos by uploader",
            description = "Retrieves the photos uploaded by a user"
    )
    @GetMapping("/uploader/{userId}")
    public ResponseEntity<List<PhotoResponse>> getPhotosByUploader(@PathVariable Long userId) {
        log.info("GET /api/photos/uploader/{}", userId);

        List<PhotoResponse> photos = photoService.getPhotosByUploader(userId);
        return ResponseEntity.ok(photos);
    }

    @Operation(
            summary = "Photo cleanup (admin)",
            description = "Permanently deletes soft-deleted photos"
    )
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupDeletedPhotos(
            @RequestParam(defaultValue = "7") int daysOld
    ) {
        log.info("POST /api/photos/cleanup?daysOld={}", daysOld);

        int cleanedCount = photoService.cleanupDeletedPhotos(daysOld);

        return ResponseEntity.ok(String.format("Cleanup completed: %d photos deleted", cleanedCount));
    }
}
