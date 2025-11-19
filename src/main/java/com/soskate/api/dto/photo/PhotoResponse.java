package com.soskate.api.dto.photo;

import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;

import java.time.LocalDateTime;

/**
 * DTO for photo responses in API.
 * Contains all public photo information.
 * Immutable record for thread-safety and clarity.
 */
public record PhotoResponse(
        Long id,
        String url,
        String thumbnailUrl,
        PhotoEntityType entityType,
        Long entityId,
        PhotoType photoType,
        String originalFileName,
        Long fileSize,
        String mimeType,
        Integer width,
        Integer height,
        Integer displayOrder,
        Long uploadedBy,
        LocalDateTime uploadedAt
) {
    /**
     * Human-readable file size (e.g., "1.2 MB", "450 KB").
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "0 B";
        }

        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    /**
     * Image aspect ratio (width:height).
     */
    public String getAspectRatio() {
        if (width == null || height == null || height == 0) {
            return "N/A";
        }

        double ratio = (double) width / height;
        return String.format("%.2f:1", ratio);
    }
}