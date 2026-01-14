package com.soskate.api.dto.spot;

import java.math.BigDecimal;

public record SpotSummary(
        Long id,
        String name,
        String address,
        String city,
        BigDecimal latitude,
        BigDecimal longitude
) {}