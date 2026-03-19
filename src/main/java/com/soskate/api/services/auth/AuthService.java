package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.ChangePasswordRequest;
import com.soskate.api.dto.auth.DeleteAccountRequest;
import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.LoginResponse;
import com.soskate.api.entities.CustomerEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.auth.BadCredentialsException;
import com.soskate.api.repositories.CustomerRepository;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.security.CustomUserDetailsService;
import com.soskate.api.security.JwtService;
import com.soskate.api.services.common.EmailValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the unified authentication service.
 *
 * Search strategy:
 * 1. First searches in Customers
 * 2. If not found, searches in Instructors
 * 3. Verifies the password
 * 4. For Instructors, verifies that the status is ACTIVE
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final InstructorRepository instructorRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;


    // Generic error message to avoid revealing whether the email exists
    private static final String BAD_CREDENTIALS_MESSAGE = "Incorrect email or password";

    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email();

        log.info("Login attempt for email: {}", email);

        // === 1. Search in Customers ===
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);

        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            log.debug("User found as Customer: {}", email);

            // Password verification
            if (!passwordEncoder.matches(loginRequest.password(), customer.getPassword())) {
                log.warn("Incorrect password for Customer: {}", email);
                throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(customer.getEmail());
            String jwtToken = jwtService.generateToken(userDetails);

            log.info("Login successful for Customer: {} (ID: {})", email, customer.getId());

            return LoginResponse.forCustomer(
                    customer.getId(),       // userId (inherited from UserEntity)
                    customer.getId(),       // customerId (same value due to JOINED inheritance)
                    customer.getEmail(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPhone(),
                    jwtToken,
                    Boolean.TRUE.equals(customer.getIsAdmin())
            );
        }

        // === 2. Search in Instructors ===
        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);

        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            log.debug("User found as Instructor: {}", email);

            // Instructor status verification
            if (instructor.getStatus() != InstructorStatus.ACTIVE) {
                log.warn("Login attempt from inactive Instructor: {} (status: {})",
                        email, instructor.getStatus());
                throw new BadCredentialsException(
                        "Your instructor account is not yet activated. " +
                                "Please check your invitation email."
                );
            }

            // Password verification
            if (!passwordEncoder.matches(loginRequest.password(), instructor.getPassword())) {
                log.warn("Incorrect password for Instructor: {}", email);
                throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(instructor.getEmail());
            String jwtToken = jwtService.generateToken(userDetails);

            log.info("Login successful for Instructor: {} (ID: {})", email, instructor.getId());

            return LoginResponse.forInstructor(
                    instructor.getId(),     // userId (inherited from UserEntity)
                    instructor.getId(),     // instructorId (same value due to JOINED inheritance)
                    instructor.getEmail(),
                    instructor.getFirstName(),
                    instructor.getLastName(),
                    instructor.getPhone(),
                    jwtToken,
                    Boolean.TRUE.equals(instructor.getIsAdmin())
            );
        }

        // === 3. No user found ===
        log.warn("No user found with email: {}", email);
        throw new BadCredentialsException(BAD_CREDENTIALS_MESSAGE);
    }

    public boolean emailExists(String email) {
        return emailValidationService.emailExists(email);
    }

    public void changePassword(String email, ChangePasswordRequest request) {

        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            if (!passwordEncoder.matches(request.currentPassword(), customer.getPassword())) {
                throw new BadCredentialsException("Mot de passe actuel incorrect");
            }
            customer.setPassword(passwordEncoder.encode(request.newPassword()));
            customerRepository.save(customer);
            log.info("Password changed for customer: {}", email);
            return;
        }

        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);
        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            if (!passwordEncoder.matches(request.currentPassword(), instructor.getPassword())) {
                throw new BadCredentialsException("Mot de passe actuel incorrect");
            }
            instructor.setPassword(passwordEncoder.encode(request.newPassword()));
            instructorRepository.save(instructor);
            log.info("Password changed for instructor: {}", email);
            return;
        }

        throw new BadCredentialsException("Utilisateur non trouvé");
    }

    public void deleteAccount(String email, DeleteAccountRequest request) {

        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
                throw new BadCredentialsException("Mot de passe incorrect");
            }
            customer.markAsDeleted();
            customerRepository.save(customer);
            log.info("Account soft-deleted for customer: {}", email);
            return;
        }

        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);
        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            if (!passwordEncoder.matches(request.password(), instructor.getPassword())) {
                throw new BadCredentialsException("Mot de passe incorrect");
            }
            instructor.markAsDeleted();
            instructorRepository.save(instructor);
            log.info("Account soft-deleted for instructor: {}", email);
            return;
        }

        throw new BadCredentialsException("Utilisateur non trouvé");
    }

}