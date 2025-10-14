package com.soskate.api.dtos.service;

import com.soskate.api.enums.ServiceType;
import jakarta.validation.constraints.*;

public record ServiceRequestDto(

        @NotBlank(message = "Name is mandatory.")
        @Size(max = 150, message = "Name cannot exceed 150 characters.")
        String name,

        @NotNull(message = "Service type is mandatory.")
        ServiceType type,

        @Size(max = 10000, message = "Description cannot exceed 10 000 characters.")
        String description,

        @Positive(message = "Duration must be strictly positive if entered.")
        Integer durationMin,

        @NotNull(message = "Base price is mandatory.")
        @Min(value = 0, message = "Base price must be greater than or equal to 0.")
        Integer basePriceCents,

        @NotNull(message = "Active/inactive status is mandatory.")
        Boolean isActive


) {

}
