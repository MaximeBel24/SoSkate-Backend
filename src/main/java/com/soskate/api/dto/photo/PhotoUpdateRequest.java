package com.soskate.api.dto.photo;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating photo metadata (not the image file itself).
 * Currently supports only display order updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoUpdateRequest {

    /**
     * New display order for gallery photos.
     * Lower numbers appear first.
     */
    @Min(value = 0, message = "L'ordre d'affichage doit être >= 0")
    private Integer displayOrder;
}