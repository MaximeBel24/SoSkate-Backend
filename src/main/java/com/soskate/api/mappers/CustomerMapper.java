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
     * @param customerRegisterRequestDTO le DTO d'inscription
     * @param hashedPassword le mot de passe déjà hashé
     * @return l'entité CustomerEntity prête à être sauvegardée
     */
    public static CustomerEntity customerRegisterRequestDTOToCustomerEntity(CustomerRegisterRequest customerRegisterRequestDTO, String hashedPassword) {
        if (customerRegisterRequestDTO == null) {
            return null;
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setEmail(customerRegisterRequestDTO.email());
        customer.setPassword(hashedPassword);
        customer.setFirstname(customerRegisterRequestDTO.firstname());
        customer.setLastname(customerRegisterRequestDTO.lastname());
        customer.setPhone(customerRegisterRequestDTO.phone());
        customer.setBirthDate(customerRegisterRequestDTO.birthDate());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setIsAdmin(false);

        return customer;
    }

    /**
     * Convertit un CustomerEntity en CustomerResponseDTO.
     * Ne contient JAMAIS le mot de passe.
     *
     * @param customerEntity l'entité à convertir
     * @return le DTO de réponse
     */
    public static CustomerResponse customerEntityToCustomerResponseDTO(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return null;
        }

        return new CustomerResponse(
                customerEntity.getId(),
                customerEntity.getEmail(),
                customerEntity.getFirstname(),
                customerEntity.getLastname(),
                customerEntity.getPhone(),
                customerEntity.getBirthDate(),
                customerEntity.getCreatedAt(),
                customerEntity.getUpdatedAt()
        );
    }

}
