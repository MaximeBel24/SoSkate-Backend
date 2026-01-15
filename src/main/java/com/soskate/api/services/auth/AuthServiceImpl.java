package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.UnifiedLoginResponse;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.auth.BadCredentialsException;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implémentation du service d'authentification unifié.
 *
 * Stratégie de recherche :
 * 1. Cherche d'abord dans les Customers
 * 2. Si non trouvé, cherche dans les Instructors
 * 3. Vérifie le mot de passe
 * 4. Pour les Instructors, vérifie que le statut est ACTIVE
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;

    // Message d'erreur générique pour ne pas révéler si l'email existe
    private static final String BAD_CREDENTIALS_MESSAGE = "Email ou mot de passe incorrect";

    @Override
    public UnifiedLoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();

        log.info("Tentative de connexion unifiée pour l'email : {}", email);

        // === 1. Chercher dans les Customers ===
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            log.debug("Utilisateur trouvé comme Customer : {}", email);

            // Vérification du mot de passe
            // TODO: Remplacer par BCrypt en production
            if (!password.equals(customer.getPassword())) {
                log.warn("Mot de passe incorrect pour le Customer : {}", email);
                throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
            }

            log.info("Connexion réussie pour le Customer : {} (ID: {})", email, customer.getId());

            return UnifiedLoginResponse.forCustomer(
                    customer.getId(),       // userId (hérité de UserEntity)
                    customer.getId(),       // customerId (même valeur car JOINED inheritance)
                    customer.getEmail(),
                    customer.getFirstname(),
                    customer.getLastname(),
                    customer.getPhone()
            );
        }

        // === 2. Chercher dans les Instructors ===
        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmail(email);

        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            log.debug("Utilisateur trouvé comme Instructor : {}", email);

            // Vérification du statut de l'instructeur
            if (instructor.getStatus() != InstructorStatus.ACTIVE) {
                log.warn("Tentative de connexion d'un Instructor non actif : {} (statut: {})",
                        email, instructor.getStatus());
                throw new BadCredentialsException(
                        "Votre compte instructeur n'est pas encore activé. " +
                                "Veuillez vérifier votre email d'invitation."
                );
            }

            // Vérification du mot de passe
            // TODO: Remplacer par BCrypt en production
            if (!password.equals(instructor.getPassword())) {
                log.warn("Mot de passe incorrect pour l'Instructor : {}", email);
                throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
            }

            log.info("Connexion réussie pour l'Instructor : {} (ID: {})", email, instructor.getId());

            return UnifiedLoginResponse.forInstructor(
                    instructor.getId(),     // userId (hérité de UserEntity)
                    instructor.getId(),     // instructorId (même valeur car JOINED inheritance)
                    instructor.getEmail(),
                    instructor.getFirstname(),
                    instructor.getLastname(),
                    instructor.getPhone()
            );
        }

        // === 3. Aucun utilisateur trouvé ===
        log.warn("Aucun utilisateur trouvé avec l'email : {}", email);
        throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
    }

    @Override
    public boolean emailExists(String email) {
        log.debug("Vérification de l'existence de l'email : {}", email);

        boolean existsAsCustomer = customerRepository.existsByEmail(email);
        if (existsAsCustomer) {
            return true;
        }

        return instructorRepository.existsByEmail(email);
    }
}