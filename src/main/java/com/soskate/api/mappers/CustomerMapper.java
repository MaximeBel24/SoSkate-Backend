package com.soskate.api.mappers;
import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.entities.CustomerEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper pour convertir entre CustomerEntity et ses DTOs.
 * Utilise des méthodes statiques pour être utilisable partout.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class CustomerMapper {

    /**
     * Convertit un CustomerRegisterDTO en CustomerEntity.
     * Le mot de passe doit être hashé AVANT d'appeler cette méthode.
     *
     * @param request le DTO d'inscription
     * @param hashedPassword le mot de passe déjà hashé
     * @return l'entité CustomerEntity prête à être sauvegardée
     */
    public static CustomerEntity toEntity(CustomerRegisterRequest request, String hashedPassword) {
        if (request == null) {
            return null;
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setEmail(request.email());
        customer.setPassword(hashedPassword);
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());
        customer.setBirthDate(request.birthDate());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setIsAdmin(false);

        return customer;
    }

    /**
     * Convertit un CustomerEntity en CustomerResponseDTO.
     * Ne contient JAMAIS le mot de passe.
     *
     * @param entity l'entité à convertir
     * @return le DTO de réponse
     */
    public static CustomerResponse toResponse(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CustomerResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
