package com.soskate.api.dtos.spot;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SpotToCreateDto(

        @NotBlank(message = "Name is mandatory.")
        @Size(max = 150, message = "Name cannot exceed 150 characters.")
        String name,

        @Size(max = 200, message = "Address line 1 cannot exceed 200 characters.")
        String addressLine1,

        @Size(max = 200, message = "Address line 2 cannot exceed 200 characters.")
        String addressLine2,

        @Size(max = 100, message = "City cannot exceed 100 characters.")
        String city,

        @Size(max = 20, message = "Postal code cannot exceed 20 characters.")
        String postalCode,

        @Size(max = 2, message = "Country must be a 2-letter ISO code.")
        String country,

        @NotNull(message = "Latitude is mandatory.")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90.")
        @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90.")
        BigDecimal latitude,

        @NotNull(message = "Longitude is mandatory.")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180.")
        @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180.")
        BigDecimal longitude,

        @NotNull(message = "Indoor flag is mandatory.")
        Boolean isIndoor,

        @NotNull(message = "Active flag is mandatory.")
        Boolean isActive
) {}
