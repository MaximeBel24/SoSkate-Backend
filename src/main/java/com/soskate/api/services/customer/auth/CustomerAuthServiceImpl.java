package com.soskate.api.services.customer.auth;

import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.mappers.CustomerMapper;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.services.common.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service d'authentification des customers.
 * Gère l'inscription avec validation, hashage du mot de passe et sauvegarde en base.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouveau customer.
     *
     * @param customerToRegister les données d'inscription
     * @return le customer créé
     * @throws EmailAlreadyExistsException si l'email existe déjà
     */
    @Override
    @Transactional
    public CustomerResponse registerCustomer(CustomerRegisterRequest customerToRegister) {
        log.info("Tentative d'inscription pour l'email : {}", customerToRegister.email());

        emailValidationService.validateEmailUnique(customerToRegister.email());

        String hashedPassword = passwordEncoder.encode(customerToRegister.password());
        log.debug("Mot de passe hashé avec succès");

        CustomerEntity customer = customerMapper.toEntity(customerToRegister, hashedPassword);

        CustomerEntity savedCustomer = customerRepository.save(customer);
        log.info("Customer créé avec succès : ID {}, Email {}",
                savedCustomer.getId(),
                savedCustomer.getEmail());

        return customerMapper.toResponse(savedCustomer);
    }

    /**
     * Vérifie si un email existe déjà en base.
     * Utile pour la validation côté frontend.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    @Override
    public boolean emailExists(String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);
        return customerRepository.existsByEmail(email);
    }
}
