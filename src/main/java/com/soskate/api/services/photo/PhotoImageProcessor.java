package com.soskate.api.services.photo;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.ImageWriter;
import com.sksamuel.scrimage.webp.WebpWriter;
import com.soskate.api.config.PhotoConfigProperties;
import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import com.soskate.api.exceptions.photo.InvalidPhotoFormatException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.repositories.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Handles image validation, processing, and WebP conversion.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PhotoImageProcessor {

    private final PhotoRepository photoRepository;
    private final PhotoConfigProperties photoConfig;

    /**
     * Result of image processing containing full and thumbnail images.
     */
    public record ProcessedImages(
            byte[] fullImage,
            byte[] thumbnailImage,
            int width,
            int height
    ) {}

    /**
     * Validates the uploaded file (size, format).
     *
     * @param file The uploaded file
     * @throws InvalidPhotoFormatException if validation fails
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPhotoFormatException("File is empty or missing");
        }

        if (file.getSize() > photoConfig.getUpload().getMaxFileSize()) {
            throw new InvalidPhotoFormatException(
                    String.format("File is too large. Max size: %d MB",
                            photoConfig.getUpload().getMaxFileSize() / (1024 * 1024))
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !photoConfig.getUpload().getAllowedFormats().contains(contentType)) {
            String originalFilename = file.getOriginalFilename();
            boolean isJfif = originalFilename != null &&
                    originalFilename.toLowerCase().endsWith(".jfif");

            if (!isJfif) {
                throw new InvalidPhotoFormatException(
                        "Unsupported file format. Accepted formats: " +
                                String.join(", ", photoConfig.getUpload().getAllowedFormats()) +
                                " (or .jfif)"
                );
            }
        }
    }

    /**
     * Validates photo limits for the given entity.
     *
     * @param entityType The entity type
     * @param entityId   The entity ID
     * @param photoType  The photo type
     * @throws PhotoUploadException if limit is reached
     */
    public void validatePhotoLimits(PhotoEntityType entityType, Long entityId, PhotoType photoType) {
        int currentCount = photoRepository.countByEntityTypeAndEntityIdAndPhotoTypeAndDeletedFalse(
                entityType, entityId, photoType
        );

        int limit = getPhotoLimit(entityType, photoType);

        if (currentCount >= limit) {
            throw new PhotoUploadException(
                    String.format("Photo limit reached for this type (%d max)", limit)
            );
        }
    }

    /**
     * Loads and validates image dimensions and aspect ratio.
     *
     * @param file The uploaded file
     * @return The loaded image
     * @throws IOException if image cannot be loaded
     * @throws InvalidPhotoFormatException if dimensions are invalid
     */
    public ImmutableImage loadAndValidateImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ImmutableImage image = ImmutableImage.loader().fromStream(inputStream);

            if (image.width < photoConfig.getUpload().getMinWidth() ||
                    image.height < photoConfig.getUpload().getMinHeight()) {
                throw new InvalidPhotoFormatException(
                        String.format("Image too small. Minimum dimensions: %dx%d",
                                photoConfig.getUpload().getMinWidth(),
                                photoConfig.getUpload().getMinHeight())
                );
            }

            double ratio = (double) image.width / image.height;
            if (ratio > photoConfig.getUpload().getMaxRatio() ||
                    ratio < 1.0 / photoConfig.getUpload().getMaxRatio()) {
                throw new InvalidPhotoFormatException(
                        String.format("Invalid image ratio (max %s:1)",
                                photoConfig.getUpload().getMaxRatio())
                );
            }

            return image;
        }
    }

    /**
     * Processes image: resize and convert to WebP format.
     *
     * @param image     The source image
     * @param photoType The photo type (affects sizing for avatars)
     * @return Processed images (full + thumbnail)
     * @throws IOException if processing fails
     */
    public ProcessedImages processImage(ImmutableImage image, PhotoType photoType) throws IOException {
        int fullMaxWidth;
        int thumbWidth;

        if (photoType == PhotoType.AVATAR) {
            fullMaxWidth = photoConfig.getAvatar().getSize();
            thumbWidth = photoConfig.getAvatarThumb().getSize();

            // Crop to square (center)
            int minDimension = Math.min(image.width, image.height);
            image = image.resizeTo(minDimension, minDimension);
        } else {
            fullMaxWidth = photoConfig.getFull().getMaxWidth();
            thumbWidth = photoConfig.getThumb().getWidth();
        }

        // Resize full image if needed
        ImmutableImage fullImage = image;
        if (image.width > fullMaxWidth) {
            fullImage = image.scaleToWidth(fullMaxWidth);
        }

        // Generate thumbnail
        ImmutableImage thumbnail = image.scaleToWidth(thumbWidth);

        // Convert to WebP
        ImageWriter fullWriter = WebpWriter.DEFAULT.withQ(photoConfig.getWebp().getQualityFull());
        ImageWriter thumbWriter = WebpWriter.DEFAULT.withQ(photoConfig.getWebp().getQualityThumb());

        return new ProcessedImages(
                fullImage.bytes(fullWriter),
                thumbnail.bytes(thumbWriter),
                fullImage.width,
                fullImage.height
        );
    }

    /**
     * Determines the S3 base path for the given entity and photo type.
     */
    public String getS3BasePath(PhotoEntityType entityType, PhotoType photoType) {
        return switch (entityType) {
            case SPOT -> "spots";
            case CUSTOMER -> "customers/avatars";
            case INSTRUCTOR -> photoType == PhotoType.AVATAR ? "instructors/avatars" : "instructors/tricks";
            case EVENT -> photoType == PhotoType.COVER ? "events/covers" : "events/gallery";
        };
    }

    private int getPhotoLimit(PhotoEntityType entityType, PhotoType photoType) {
        if (photoType == PhotoType.AVATAR || photoType == PhotoType.COVER) {
            return 1;
        }

        return switch (entityType) {
            case SPOT -> photoType == PhotoType.GALLERY ? photoConfig.getLimits().getSpotGallery() : 999;
            case INSTRUCTOR -> photoType == PhotoType.TRICK ? photoConfig.getLimits().getInstructorTricks() : 999;
            case EVENT -> photoType == PhotoType.GALLERY ? photoConfig.getLimits().getEventGallery() : 999;
            default -> 999;
        };
    }
}
