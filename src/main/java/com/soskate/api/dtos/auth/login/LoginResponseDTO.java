package com.soskate.api.dtos.auth.login;

import com.soskate.api.dtos.customer.CustomerResponseDTO;

/**
 * DTO de réponse après connexion réussie.
 * Version simplifiée sans token JWT (pour le dev).
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record LoginResponseDTO(
        CustomerResponseDTO customer,
        String message
) {

    public LoginResponseDTO(CustomerResponseDTO customer) {
        this(customer, "Connexion réussie");
    }
}