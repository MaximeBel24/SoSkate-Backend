package com.soskate.api.services.photo;

import com.sksamuel.scrimage.ImmutableImage;
import com.soskate.api.dto.photo.*;
import com.soskate.api.entities.PhotoEntity;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import com.soskate.api.exceptions.photo.PhotoNotFoundException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.mappers.PhotoMapper;
import com.soskate.api.repositories.PhotoRepository;
import com.soskate.api.services.storage.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of PhotoService.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final S3Service s3Service;
    private final PhotoMapper photoMapper;
    private final PhotoImageProcessor imageProcessor;

    // ========== UPLOAD ==========

    @Override
    @Transactional
    public PhotoResponse uploadPhoto(PhotoUploadRequest request) {
        log.info("Starting photo upload: entityType={}, entityId={}, photoType={}",
                request.getEntityType(), request.getEntityId(), request.getPhotoType());

        MultipartFile file = request.getFile();

        // Step 1: Validate file
        imageProcessor.validateFile(file);

        // Step 2: Handle avatar/cover replacement
        if (request.getPhotoType() == PhotoType.AVATAR || request.getPhotoType() == PhotoType.COVER) {
            deleteExistingAvatarOrCover(request.getEntityType(), request.getEntityId(),
                    request.getPhotoType(), request.getUploadedBy());
        }

        // Step 3: Check photo limits
        imageProcessor.validatePhotoLimits(request.getEntityType(), request.getEntityId(), request.getPhotoType());

        try {
            // Step 4: Load and validate image
            ImmutableImage image = imageProcessor.loadAndValidateImage(file);

            // Step 5: Convert to WebP and generate thumbnail
            PhotoImageProcessor.ProcessedImages processed = imageProcessor.processImage(image, request.getPhotoType());

            // Step 6: Generate S3 paths
            String basePath = imageProcessor.getS3BasePath(request.getEntityType(), request.getPhotoType());
            String fullFileName = s3Service.generateUniqueFileName(file.getOriginalFilename(), basePath + "/full");
            String thumbFileName = s3Service.generateUniqueFileName(file.getOriginalFilename(), basePath + "/thumb");

            // Step 7: Upload to S3
            String fullUrl = uploadToS3(processed.fullImage(), fullFileName);
            String thumbUrl = uploadToS3(processed.thumbnailImage(), thumbFileName);

            // Step 8: Save metadata to database
            PhotoEntity photoEntity = buildPhotoEntity(request, file, processed, fullUrl, thumbUrl);
            PhotoEntity savedPhoto = photoRepository.save(photoEntity);

            log.info("Photo uploaded successfully: id={}, url={}", savedPhoto.getId(), savedPhoto.getUrl());
            return photoMapper.toResponse(savedPhoto);

        } catch (IOException e) {
            log.error("Failed to process image: {}", e.getMessage(), e);
            throw new PhotoUploadException("Error processing image: " + e.getMessage(), e);
        }
    }

    private void deleteExistingAvatarOrCover(PhotoEntityType entityType, Long entityId,
                                             PhotoType photoType, Long deletedBy) {
        photoRepository.findFirstByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
                entityType, entityId, photoType
        ).ifPresent(existingPhoto -> {
            log.info("Replacing existing {} for entityType={}, entityId={}: deleting photo id={}",
                    photoType, entityType, entityId, existingPhoto.getId());
            deletePhoto(existingPhoto.getId(), deletedBy);
        });
    }

    private String uploadToS3(byte[] imageBytes, String fileName) {
        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return s3Service.uploadFile(inputStream, fileName, "image/webp", imageBytes.length);
        } catch (IOException e) {
            throw new PhotoUploadException("Error uploading to S3", e);
        }
    }

    private PhotoEntity buildPhotoEntity(PhotoUploadRequest request, MultipartFile file,
                                         PhotoImageProcessor.ProcessedImages processed,
                                         String fullUrl, String thumbUrl) {
        return PhotoEntity.builder()
                .url(fullUrl)
                .thumbnailUrl(thumbUrl)
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .photoType(request.getPhotoType())
                .originalFileName(file.getOriginalFilename())
                .fileSize((long) processed.fullImage().length)
                .mimeType("image/webp")
                .width(processed.width())
                .height(processed.height())
                .displayOrder(request.getDisplayOrder())
                .uploadedBy(request.getUploadedBy())
                .build();
    }

    // ========== READ ==========

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

    // ========== UPDATE ==========

    @Override
    @Transactional
    public PhotoResponse updatePhoto(Long photoId, PhotoUpdateRequest request) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        if (request.displayOrder() != null) {
            photo.setDisplayOrder(request.displayOrder());
        }

        PhotoEntity updatedPhoto = photoRepository.save(photo);
        log.info("Photo updated: id={}", updatedPhoto.getId());

        return photoMapper.toResponse(updatedPhoto);
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId, Long deletedBy) {
        PhotoEntity photo = photoRepository.findById(photoId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        photo.markAsDeleted(deletedBy);
        photoRepository.save(photo);

        log.info("Photo soft-deleted: id={}, deletedBy={}", photoId, deletedBy);
    }

    @Override
    @Transactional
    public int cleanupDeletedPhotos(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<PhotoEntity> photosToCleanup = photoRepository.findDeletedPhotosOlderThan(cutoffDate);

        int cleanedCount = 0;
        for (PhotoEntity photo : photosToCleanup) {
            try {
                String fullKey = photo.getS3Key();
                String thumbKey = photo.getThumbnailS3Key();

                if (fullKey != null) {
                    s3Service.deleteFile(fullKey);
                }
                if (thumbKey != null) {
                    s3Service.deleteFile(thumbKey);
                }

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
