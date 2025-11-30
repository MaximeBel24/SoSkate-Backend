package com.soskate.api.dto.spot;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO pour créer ou modifier un spot.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record SpotRequest(

        @NotBlank(message = "Le nom du spot est obligatoire")
        @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
        String name,

        @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
        String description,

        @NotBlank(message = "L'adresse est obligatoire")
        @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
        String address,

        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
        String city,

        @NotBlank(message = "Le code postal est obligatoire")
        @Pattern(
                regexp = "^\\d{5}$",
                message = "Le code postal doit être composé de 5 chiffres"
        )
        String zipCode,

        @NotNull(message = "La latitude est obligatoire")
        @DecimalMin(value = "-90.0", message = "La latitude doit être comprise entre -90 et 90")
        @DecimalMax(value = "90.0", message = "La latitude doit être comprise entre -90 et 90")
        BigDecimal latitude,

        @NotNull(message = "La longitude est obligatoire")
        @DecimalMin(value = "-180.0", message = "La longitude doit être comprise entre -180 et 180")
        @DecimalMax(value = "180.0", message = "La longitude doit être comprise entre -180 et 180")
        BigDecimal longitude,

        @NotNull(message = "Le type de spot (intérieur/extérieur) est obligatoire")
        Boolean isIndoor,

        @NotNull(message = "Le statut actif/inactif est obligatoire")
        Boolean isActive
) {}