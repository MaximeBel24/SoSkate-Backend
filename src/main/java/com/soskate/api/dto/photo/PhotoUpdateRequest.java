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
public record PhotoUpdateRequest(
        @Min(value = 0, message = "L'ordre d'affichage doit être >= 0")
        Integer displayOrder
) {}