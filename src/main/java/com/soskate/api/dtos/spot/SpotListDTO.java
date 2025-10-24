package com.soskate.api.dtos.spot;

import java.math.BigDecimal;

/**
 * DTO simplifié pour afficher les spots sur une carte.
 * Contient uniquement les infos essentielles pour les markers.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record SpotListDTO(
        Long id,
        String name,
        String city,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isIndoor,
        Integer eventCount  // Nombre d'événements à venir
) {}