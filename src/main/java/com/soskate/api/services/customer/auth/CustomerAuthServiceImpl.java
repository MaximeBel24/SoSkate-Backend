package com.soskate.api.services.customer.auth;

import com.soskate.api.dtos.auth.login.LoginRequestDTO;
import com.soskate.api.dtos.auth.login.LoginResponseDTO;
import com.soskate.api.dtos.auth.register.CustomerRegisterRequestDTO;
import com.soskate.api.dtos.customer.CustomerResponseDTO;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.exceptions.auth.BadCredentialsException;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.mappers.CustomerMapper;
import com.soskate.api.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CustomerAuthServiceImpl implements CustomerAuthService{

    private final CustomerRepository customerRepository;


    /**
     * Inscrit un nouveau customer.
     * Version simplifiée : le password n'est PAS hashé (phase de dev uniquement).
     *
     * @param customerRegisterDto les données d'inscription
     * @return le customer créé
     * @throws EmailAlreadyExistsException si l'email existe déjà
     */
    @Override
    @Transactional
    public CustomerResponseDTO registerCustomer(CustomerRegisterRequestDTO customerRegisterDto) {
        log.info("Tentative d'inscription pour l'email : {}", customerRegisterDto.email());

        if (customerRepository.existsByEmail(customerRegisterDto.email())) {
            log.warn("Tentative d'inscription avec un email déjà existant : {}", customerRegisterDto.email());
            throw new EmailAlreadyExistsException(customerRegisterDto.email(), true);
        }

        // TODO: Implémenter le hashage avec BCrypt en production
//        String hashedPassword = passwordEncoder.encode(customerRegisterDto.password());
//        log.debug("Mot de passe hashé avec succès");

        CustomerEntity customer = CustomerMapper.customerRegisterRequestDTOToCustomerEntity(customerRegisterDto, customerRegisterDto.password());

        CustomerEntity savedCustomer = customerRepository.save(customer);
        log.info("Customer créé avec succès : ID {}, Email {}",
                savedCustomer.getId(),
                savedCustomer.getEmail());

        return CustomerMapper.customerEntityToCustomerResponseDTO(savedCustomer);
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

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {

        log.info("Tentative de connexion pour l'email : {}", loginRequest.email());
        CustomerEntity customer = customerRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> {
                    log.warn("Tentative de connexion avec un email inexistant : {}", loginRequest.email());
                    return new BadCredentialsException("Email ou mot de passe incorrect");
                });

        // TODO: Remplacer par BCrypt en production
        if (!loginRequest.password().equals(customer.getPassword())) {
            log.warn("Tentative de connexion avec un mot de passe incorrect pour l'email : {}", loginRequest.email());
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        log.info("Connexion réussie pour l'email : {}", loginRequest.email());

        CustomerResponseDTO customerResponse = CustomerMapper.customerEntityToCustomerResponseDTO(customer);

        return new LoginResponseDTO(customerResponse);
    }
}
