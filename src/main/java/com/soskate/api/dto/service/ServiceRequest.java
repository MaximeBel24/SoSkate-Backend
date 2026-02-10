package com.soskate.api.dto.service;

import com.soskate.api.enums.ServiceType;
import jakarta.validation.constraints.*;

public record ServiceRequest(

        @NotBlank(message = "Name is required.")
        @Size(max = 150, message = "Name cannot exceed 150 characters.")
        String name,

        @NotNull(message = "Service type is required.")
        ServiceType type,

        @Size(max = 10000, message = "Description cannot exceed 10,000 characters.")
        String description,

        @NotNull(message = "Base price is required.")
        @Min(value = 0, message = "Base price must be greater than or equal to 0.")
        Integer basePriceCents,

        @NotNull(message = "Active/inactive status is required.")
        Boolean isActive

) {

}
