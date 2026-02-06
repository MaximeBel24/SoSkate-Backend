package com.soskate.api.entities;

import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Photo entity with polymorphic relations to Spot, Customer, Instructor, Event.
 * Stores metadata and S3 URLs for full-size and thumbnail images.
 */
@Entity
@Table(
        name = "photos",
        indexes = {
                @Index(name = "idx_entity_type_id", columnList = "entity_type,entity_id"),
                @Index(name = "idx_photo_type", columnList = "photo_type"),
                @Index(name = "idx_deleted", columnList = "deleted")
        }
)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Entity type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 20)
    private PhotoEntityType entityType;

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotNull(message = "Photo type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "photo_type", nullable = false, length = 20)
    private PhotoType photoType;

    @NotBlank(message = "Photo URL is required")
    @Column(nullable = false, length = 500)
    private String url;

    @NotBlank(message = "Thumbnail URL is required")
    @Column(name = "thumbnail_url", nullable = false, length = 500)
    private String thumbnailUrl;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @NotNull
    @Min(value = 1, message = "File size must be positive")
    @Column(name = "file_size", nullable = false)
    private Long fileSize; // in bytes

    @Column(name = "mime_type", length = 50)
    private String mimeType; // "image/webp"

    @Min(value = 1, message = "Width must be positive")
    @Column(nullable = false)
    private Integer width;

    @Min(value = 1, message = "Height must be positive")
    @Column(nullable = false)
    private Integer height;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "uploaded_by")
    private Long uploadedBy; // User ID who uploaded

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    /**
     * Soft delete this photo.
     */
    public void markAsDeleted(Long deletedByUserId) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId;
    }

    /**
     * Check if this is an avatar photo.
     */
    public boolean isAvatar() {
        return this.photoType == PhotoType.AVATAR;
    }

    /**
     * Check if this is a cover photo.
     */
    public boolean isCover() {
        return this.photoType == PhotoType.COVER;
    }

    /**
     * Check if this is a gallery photo.
     */
    public boolean isGallery() {
        return this.photoType == PhotoType.GALLERY;
    }

    /**
     * Get the S3 key (path without domain) from the full URL.
     * Example: https://soskate-photos.s3.eu-west-3.amazonaws.com/spots/full/123.webp
     * Returns: spots/full/123.webp
     */
    public String getS3Key() {
        if (url == null || !url.contains(".amazonaws.com/")) {
            return null;
        }
        return url.substring(url.indexOf(".amazonaws.com/") + 15);
    }

    /**
     * Get the thumbnail S3 key.
     */
    public String getThumbnailS3Key() {
        if (thumbnailUrl == null || !thumbnailUrl.contains(".amazonaws.com/")) {
            return null;
        }
        return thumbnailUrl.substring(thumbnailUrl.indexOf(".amazonaws.com/") + 15);
    }
}