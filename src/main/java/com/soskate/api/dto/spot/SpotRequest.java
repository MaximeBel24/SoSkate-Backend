package com.soskate.api.dto.spot;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a spot.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record SpotRequest(

        @NotBlank(message = "Spot name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @NotBlank(message = "Zip code is required")
        @Pattern(
                regexp = "^\\d{5}$",
                message = "Zip code must consist of 5 digits"
        )
        String zipCode,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        BigDecimal longitude,

        @NotNull(message = "Spot type (indoor/outdoor) is required")
        Boolean isIndoor,

        @NotNull(message = "Active/inactive status is required")
        Boolean isActive
) {}
