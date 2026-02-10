package com.soskate.api.mappers;
import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.entities.CustomerEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between CustomerEntity and its DTOs.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Component
public class CustomerMapper {

    /**
     * Converts a CustomerRegisterDTO to a CustomerEntity.
     * The password must be hashed BEFORE calling this method.
     *
     * @param request the registration DTO
     * @param hashedPassword the already hashed password
     * @return the CustomerEntity ready to be saved
     */
    public CustomerEntity toEntity(CustomerRegisterRequest request, String hashedPassword) {
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
     * Converts a CustomerEntity to a CustomerResponseDTO.
     * NEVER contains the password.
     *
     * @param entity the entity to convert
     * @return the response DTO
     */
    public CustomerResponse toResponse(CustomerEntity entity) {
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
