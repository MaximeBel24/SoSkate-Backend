package com.soskate.api.services.customer;

import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.dto.customer.CustomerUpdateRequest;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.mappers.CustomerMapper;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service de gestion du profil Customer.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request) {
        log.info("Mise à jour du profil pour le client ID: {}", customerId);

        // 1. Récupérer le customer existant
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Client non trouvé avec l'ID: {}", customerId);
                    return new CustomerNotFoundException("Client non trouvé avec l'id : " + customerId);
                });

        // 2. Vérifier l'unicité de l'email si modifié
        if (request.email() != null && !request.email().equalsIgnoreCase(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.email())) {
                log.warn("Tentative de mise à jour avec un email déjà existant (client): {}", request.email());
                throw new EmailAlreadyExistsException(request.email(), true);
            }
            if (instructorRepository.existsByEmail(request.email())) {
                log.warn("Tentative de mise à jour avec un email déjà existant (professeur): {}", request.email());
                throw new EmailAlreadyExistsException(request.email(), true);
            }
        }

        // 3. Mise à jour partielle (seuls les champs non-null)
        if (request.firstname() != null) {
            customer.setFirstName(request.firstname());
            log.debug("Prénom mis à jour: {}", request.firstname());
        }

        if (request.lastname() != null) {
            customer.setLastName(request.lastname());
            log.debug("Nom mis à jour: {}", request.lastname());
        }

        if (request.email() != null) {
            customer.setEmail(request.email());
            log.debug("Email mis à jour: {}", request.email());
        }

        if (request.phone() != null) {
            customer.setPhone(request.phone());
            log.debug("Téléphone mis à jour: {}", request.phone());
        }

        if (request.birthDate() != null) {
            customer.setBirthDate(request.birthDate());
            log.debug("Date de naissance mise à jour: {}", request.birthDate());
        }

        // 4. Sauvegarder et retourner
        CustomerEntity updatedCustomer = customerRepository.save(customer);
        log.info("Profil mis à jour avec succès pour le customer ID: {}", customerId);

        return CustomerMapper.toResponse(updatedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        log.debug("Récupération du profil pour le customer ID: {}", customerId);

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer non trouvé avec l'ID: {}", customerId);
                    return new CustomerNotFoundException("Client non trouvé avec l'id : " + customerId);
                });

        return CustomerMapper.toResponse(customer);
    }
}