package com.soskate.api.dto.auth.login;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO de réponse unifié après connexion réussie.
 * Supporte à la fois les Customers et les Instructors.
 *
 * Le champ `role` permet au frontend de déterminer le type d'utilisateur
 * et d'adapter l'interface en conséquence.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UnifiedLoginResponse(

        // === Identifiants ===
        Long id,                    // ID de l'entité User (parent)
        Long customerId,            // ID spécifique si Customer (null sinon)
        Long instructorId,          // ID spécifique si Instructor (null sinon)

        // === Infos utilisateur ===
        String email,
        String firstName,
        String lastName,
        String phone,

        // === Rôle ===
        UserRole role,

        // === Message ===
        String message
) {

    /**
     * Enum pour identifier le type d'utilisateur.
     */
    public enum UserRole {
        CUSTOMER,
        INSTRUCTOR
    }

    /**
     * Factory method pour créer une réponse Customer.
     */
    public static UnifiedLoginResponse forCustomer(
            Long userId,
            Long customerId,
            String email,
            String firstName,
            String lastName,
            String phone
    ) {
        return new UnifiedLoginResponse(
                userId,
                customerId,
                null,  // instructorId
                email,
                firstName,
                lastName,
                phone,
                UserRole.CUSTOMER,
                "Connexion réussie"
        );
    }

    /**
     * Factory method pour créer une réponse Instructor.
     */
    public static UnifiedLoginResponse forInstructor(
            Long userId,
            Long instructorId,
            String email,
            String firstName,
            String lastName,
            String phone
    ) {
        return new UnifiedLoginResponse(
                userId,
                null,  // customerId
                instructorId,
                email,
                firstName,
                lastName,
                phone,
                UserRole.INSTRUCTOR,
                "Connexion réussie"
        );
    }
}