package com.soskate.api.dto.photo;

import com.soskate.api.enums.PhotoEntityType;
import com.soskate.api.enums.PhotoType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for uploading a new photo.
 * Used for multipart/form-data requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoUploadRequest {

    @NotNull(message = "Le fichier est requis")
    private MultipartFile file;

    @NotNull(message = "Le type d'entité est requis")
    private PhotoEntityType entityType;

    @NotNull(message = "L'ID de l'entité est requis")
    @Positive(message = "L'ID de l'entité doit être positif")
    private Long entityId;

    @NotNull(message = "Le type de photo est requis")
    private PhotoType photoType;

    /**
     * Display order for gallery photos (optional, defaults to 0).
     * Ignored for AVATAR and COVER photos.
     */
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * ID of the user uploading the photo (set by the service from authentication context).
     */
    private Long uploadedBy;
}