package com.soskate.api.dtos.service;

import com.soskate.api.enums.ServiceType;
import jakarta.validation.constraints.*;

public record ServiceRequestDTO(

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 150, message = "Le nom ne peut excéder 150 caractères.")
        String name,

        @NotNull(message = "Le type de service est obligatoire.")
        ServiceType type,

        @Size(max = 10000, message = "La description ne peut excéder 10 000 caractères.")
        String description,

        @Positive(message = "La durée doit être strictement positive si elle est saisie.")
        Integer durationMin,

        @NotNull(message = "Le prix de base est obligatoire.")
        @Min(value = 0, message = "Le prix de base doit être supérieur ou égal à 0.")
        Integer basePriceCents,

        @NotNull(message = "Le statut actif/inactif est obligatoire.")
        Boolean isActive


) {

}
