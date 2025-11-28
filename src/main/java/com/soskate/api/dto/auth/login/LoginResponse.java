package com.soskate.api.dto.auth.login;

import com.soskate.api.dto.customer.CustomerResponse;

/**
 * DTO de réponse après connexion réussie.
 * Version simplifiée sans token JWT (pour le dev).
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record LoginResponse(
        CustomerResponse customer,
        String message
) {

    public LoginResponse(CustomerResponse customer) {
        this(customer, "Connexion réussie");
    }
}