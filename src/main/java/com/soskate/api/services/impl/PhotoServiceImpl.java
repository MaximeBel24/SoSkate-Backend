package com.soskate.api.services.impl;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.webp.WebpWriter;
import com.soskate.api.config.PhotoConfigProperties;
import com.soskate.api.dto.photo.*;
import com.soskate.api.entities.PhotoEntity;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import com.soskate.api.exceptions.photo.InvalidPhotoFormatException;
import com.soskate.api.exceptions.photo.PhotoNotFoundException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.repositories.PhotoRepository;
import com.soskate.api.service.PhotoService;
import com.soskate.api.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of PhotoService.
 * Handles photo upload with WebP conversion using Scrimage and S3 storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final PhotoMapper photoMapper;
    private final PhotoConfigProperties photoConfig;

    // ========== UPLOAD PHOTO ==========

    @Override
    @Transactional
    public PhotoResponse uploadPhoto(PhotoUploadRequest request) {
        log.info("Starting photo upload: entityType={}, entityId={}, photoType={}",
                request.getEntityType(), request.getEntityId(), request.getPhotoType());

        MultipartFile file = request.getFile();

        // Step 1: Validate file
        validateFile(file);

        // Step 2: Check photo limits
        validatePhotoLimits(request.getEntityType(), request.getEntityId(), request.getPhotoType());

        // Step 3: Handle avatar replacement (delete old avatar if exists)
        if (request.getPhotoType() == PhotoType.AVATAR) {
            deleteExistingAvatar(request.getEntityType(), request.getEntityId(), request.getUploadedBy());
        }

        try {
            // Step 4: Load and validate image
            ImmutableImage image = loadAndValidateImage(file);

            // Step 5: Convert to WebP and generate thumbnail
            ProcessedImages processedImages = processImage(image, request.getPhotoType());

            // Step 6: Generate S3 paths
            String basePath = getS3BasePath(request.getEntityType(), request.getPhotoType());
            String fullFileName = s3Service.generateUniqueFileName(file.getOriginalFilename(), basePath + "/full");
            String thumbFileName = s3Service.generateUniqueFileName(file.getOriginalFilename(), basePath + "/thumb");

            // Step 7: Upload to S3
            String fullUrl = uploadToS3(processedImages.fullImage, fullFileName);
            String thumbUrl = uploadToS3(processedImages.thumbnailImage, thumbFileName);

            // Step 8: Save metadata to database
            PhotoEntity photoEntity = buildPhotoEntity(request, file, processedImages, fullUrl, thumbUrl);
            PhotoEntity savedPhoto = photoRepository.save(photoEntity);

            log.info("Photo uploaded successfully: id={}, url={}", savedPhoto.getId(), savedPhoto.getUrl());
            return photoMapper.toResponse(savedPhoto);

        } catch (IOException e) {
            log.error("Failed to process image: {}", e.getMessage(), e);
            throw new PhotoUploadException("Erreur lors du traitement de l'image: " + e.getMessage(), e);
        }
    }

    // ========== VALIDATION METHODS ==========

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPhotoFormatException("Le fichier est vide ou manquant");
        }

        // Check file size
        if (file.getSize() > photoConfig.getUpload().getMaxFileSize()) {
            throw new InvalidPhotoFormatException(
                    String.format("Le fichier est trop volumineux. Taille max: %d MB",
                            photoConfig.getUpload().getMaxFileSize() / (1024 * 1024))
            );
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !photoConfig.getUpload().getAllowedFormats().contains(contentType)) {
            // Special case: JFIF files often have image/jpeg as content-type
            // Also check original filename extension
            String originalFilename = file.getOriginalFilename();
            boolean isJfif = originalFilename != null &&
                    originalFilename.toLowerCase().endsWith(".jfif");

            if (!isJfif) {
                throw new InvalidPhotoFormatException(
                        "Format de fichier non supporté. Formats acceptés: " +
                                String.join(", ", photoConfig.getUpload().getAllowedFormats()) +
                                " (ou .jfif)"
                );
            }
            }
    }

    private void validatePhotoLimits(PhotoEntityType entityType, Long entityId, PhotoType photoType) {
        // Get current photo count
        int currentCount = photoRepository.countByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
                entityType, entityId, photoType
        );

        // Get limit based on entity type and photo type
        int limit = getPhotoLimit(entityType, photoType);

        if (currentCount >= limit) {
            throw new PhotoUploadException(
                    String.format("Limite de photos atteinte pour ce type (%d max)", limit)
            );
        }
    }

    private int getPhotoLimit(PhotoEntityType entityType, PhotoType photoType) {
        // AVATAR and COVER are always limited to 1
        if (photoType == PhotoType.AVATAR || photoType == PhotoType.COVER) {
            return 1;
        }

        // Gallery and trick limits depend on entity type
        return switch (entityType) {
            case SPOT -> photoType == PhotoType.GALLERY ? photoConfig.getLimits().getSpotGallery() : 999;
            case INSTRUCTOR -> photoType == PhotoType.TRICK ? photoConfig.getLimits().getInstructorTricks() : 999;
            case EVENT -> photoType == PhotoType.GALLERY ? photoConfig.getLimits().getEventGallery() : 999;
            default -> 999; // No limit for other cases
        };
    }

    private void deleteExistingAvatar(PhotoEntityType entityType, Long entityId, Long deletedBy) {
        photoRepository.findFirstByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
                entityType, entityId, PhotoType.AVATAR
        ).ifPresent(existingAvatar -> {
            log.info("Deleting existing avatar: id={}", existingAvatar.getId());
            deletePhoto(existingAvatar.getId(), deletedBy);
        });
    }

    // ========== IMAGE PROCESSING ==========

    private ImmutableImage loadAndValidateImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ImmutableImage image = ImmutableImage.loader().fromStream(inputStream);

            // Validate dimensions
            if (image.width < photoConfig.getUpload().getMinWidth() ||
                    image.height < photoConfig.getUpload().getMinHeight()) {
                throw new InvalidPhotoFormatException(
                        String.format("Image trop petite. Dimensions minimales: %dx%d",
                                photoConfig.getUpload().getMinWidth(),
                                photoConfig.getUpload().getMinHeight())
                );
            }

            // Validate aspect ratio
            double ratio = (double) image.width / image.height;
            if (ratio > photoConfig.getUpload().getMaxRatio() || ratio < 1.0 / photoConfig.getUpload().getMaxRatio()) {
                throw new InvalidPhotoFormatException(
                        String.format("Ratio d'image invalide (max %s:1)", photoConfig.getUpload().getMaxRatio())
                );
            }

            return image;
        }
    }

    private ProcessedImages processImage(ImmutableImage image, PhotoType photoType) throws IOException {
        ProcessedImages result = new ProcessedImages();

        // Determine target sizes based on photo type
        int fullMaxWidth;
        int thumbWidth;
        int thumbHeight;

        if (photoType == PhotoType.AVATAR) {
            // Square crop for avatars
            fullMaxWidth = photoConfig.getAvatar().getSize();
            thumbWidth = photoConfig.getAvatarThumb().getSize();
            thumbHeight = photoConfig.getAvatarThumb().getSize();

            // Crop to square (center)
            int minDimension = Math.min(image.width, image.height);
            image = image.resizeTo(minDimension, minDimension);
        } else {
            fullMaxWidth = photoConfig.getFull().getMaxWidth();
            thumbWidth = photoConfig.getThumb().getWidth();
            // Maintain aspect ratio for thumbnail
            thumbHeight = (int) ((double) thumbWidth / image.width * image.height);
        }

        // Resize full image if needed
        ImmutableImage fullImage = image;
        if (image.width > fullMaxWidth) {
            int newHeight = (int) ((double) fullMaxWidth / image.width * image.height);
            fullImage = image.scaleToWidth(fullMaxWidth);
        }

        // Generate thumbnail
        ImmutableImage thumbnail = image.scaleToWidth(thumbWidth);

        // Convert to WebP
        ImageWriter fullWriter = WebpWriter.DEFAULT.withQ(photoConfig.getWebp().getQualityFull());
        ImageWriter thumbWriter = WebpWriter.DEFAULT.withQ(photoConfig.getWebp().getQualityThumb());

        result.fullImage = fullImage.bytes(fullWriter);
        result.thumbnailImage = thumbnail.bytes(thumbWriter);
        result.width = fullImage.width;
        result.height = fullImage.height;

        return result;
    }

    // ========== S3 UPLOAD ==========

    private String uploadToS3(byte[] imageBytes, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return s3Service.uploadFile(inputStream, fileName, "image/webp", imageBytes.length);
        } catch (IOException e) {
            throw new PhotoUploadException("Erreur lors de l'upload vers S3", e);
        }
    }

    private String getS3BasePath(PhotoEntityType entityType, PhotoType photoType) {
        String entityPath = switch (entityType) {
            case SPOT -> "spots";
            case CUSTOMER -> "customers/avatars";
            case INSTRUCTOR -> photoType == PhotoType.AVATAR ? "instructors/avatars" : "instructors/tricks";
            case EVENT -> photoType == PhotoType.COVER ? "events/covers" : "events/gallery";
        };
        return entityPath;
    }

    // ========== ENTITY BUILDING ==========

    private PhotoEntity buildPhotoEntity(PhotoUploadRequest request, MultipartFile file,
                                         ProcessedImages processed, String fullUrl, String thumbUrl) {
        return PhotoEntity.builder()
                .url(fullUrl)
                .thumbnailUrl(thumbUrl)
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .photoType(request.getPhotoType())
                .originalFileName(file.getOriginalFilename())
                .fileSize((long) processed.fullImage.length)
                .mimeType("image/webp")
                .width(processed.width)
                .height(processed.height)
                .displayOrder(request.getDisplayOrder())
                .uploadedBy(request.getUploadedBy())
                .build();
    }

    // ========== HELPER CLASS ==========

    private static class ProcessedImages {
        byte[] fullImage;
        byte[] thumbnailImage;
        int width;
        int height;
    }

    // ========== GET METHODS ==========

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse getPhotoById(Long photoId) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotosByEntity(PhotoEntityType entityType, Long entityId) {
        List<PhotoEntity> photos = photoRepository
                .findByEntityTypeAndEntityIdAndDeletedFalseOrderByDisplayOrderAsc(entityType, entityId);

        return photoMapper.toResponseList(photos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotosByEntityAndType(PhotoEntityType entityType, Long entityId, PhotoType photoType) {
        List<PhotoEntity> photos = photoRepository
                .findByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(entityType, entityId, photoType);

        return photoMapper.toResponseList(photos);
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoResponse getAvatarByEntity(PhotoEntityType entityType, Long entityId) {
        return photoRepository
                .findFirstByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(entityType, entityId, PhotoType.AVATAR)
                .map(photoMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoListResponse getAllPhotos(Pageable pageable) {
        Page<PhotoEntity> photoPage = photoRepository.findByDeletedFalse(pageable);

        return photoMapper.toListResponse(
                photoPage.getContent(),
                (int) photoPage.getTotalElements(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotosByUploader(Long userId) {
        List<PhotoEntity> photos = photoRepository
                .findByUploadedByAndDeletedFalseOrderByUploadedAtDesc(userId);

        return photoMapper.toResponseList(photos);
    }

    // ========== UPDATE METHOD ==========

    @Override
    @Transactional
    public PhotoResponse updatePhoto(Long photoId, PhotoUpdateRequest request) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        // Update display order if provided
        if (request.getDisplayOrder() != null) {
            photo.setDisplayOrder(request.getDisplayOrder());
        }

        PhotoEntity updatedPhoto = photoRepository.save(photo);
        log.info("Photo updated: id={}", updatedPhoto.getId());

        return photoMapper.toResponse(updatedPhoto);
    }

    // ========== DELETE METHOD ==========

    @Override
    @Transactional
    public void deletePhoto(Long photoId, Long deletedBy) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        // Soft delete
        photo.markAsDeleted(deletedBy);
        photoRepository.save(photo);

        log.info("Photo soft-deleted: id={}, deletedBy={}", photoId, deletedBy);

        // TODO: Schedule async S3 deletion (or handle in batch job)
        // For now, we keep the files on S3 until cleanup job runs
    }

    // ========== CLEANUP METHOD ==========

    @Override
    @Transactional
    public int cleanupDeletedPhotos(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<PhotoEntity> photosToCleanup = photoRepository.findDeletedPhotosOlderThan(cutoffDate);

        int cleanedCount = 0;
        for (PhotoEntity photo : photosToCleanup) {
            try {
                // Delete from S3
                String fullKey = photo.getS3Key();
                String thumbKey = photo.getThumbnailS3Key();

                if (fullKey != null) {
                    s3Service.deleteFile(fullKey);
                }
                if (thumbKey != null) {
                    s3Service.deleteFile(thumbKey);
                }

                // Hard delete from database
                photoRepository.delete(photo);
                cleanedCount++;

                log.info("Cleaned up photo: id={}, deletedAt={}", photo.getId(), photo.getDeletedAt());

            } catch (Exception e) {
                log.error("Failed to cleanup photo id={}: {}", photo.getId(), e.getMessage(), e);
            }
        }

        log.info("Cleanup completed: {} photos cleaned", cleanedCount);
        return cleanedCount;
    }
}