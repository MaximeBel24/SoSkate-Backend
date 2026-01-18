package com.soskate.api.dto.customer;

public record CustomerSummary(
        Long id,
        String firstName,
        String lastName,
        String email
) {}