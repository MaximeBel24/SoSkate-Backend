package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.*;
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
import com.soskate.api.services.email.EmailService;
import com.soskate.api.services.token.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final EmailService emailService;
    private final TokenGeneratorService tokenGeneratorService;

    private final int RESET_TOKEN_AVAILABILITY = 15;

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

    public boolean verifyPassword(String email, VerifyPasswordRequest request) {
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);
        if (customerOpt.isPresent()) {
            return passwordEncoder.matches(request.password(), customerOpt.get().getPassword());
        }

        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);
        if (instructorOpt.isPresent()) {
            return passwordEncoder.matches(request.password(), instructorOpt.get().getPassword());
        }

        return false;
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.email();
        log.info("Password reset requested for: {}", email);

        // === 1. Search in Customers ===
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            String code = tokenGeneratorService.generateResetCode();
            customer.setResetToken(code);
            customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(RESET_TOKEN_AVAILABILITY));
            customerRepository.save(customer);
            emailService.sendPasswordResetEmail(email, customer.getFirstName(), code);
            log.info("Reset code generated for customer: {}", email);
            return;
        }

        // === 2. Search in Instructors ===
        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);
        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            String code = tokenGeneratorService.generateResetCode();
            instructor.setResetToken(code);
            instructor.setResetTokenExpiry(LocalDateTime.now().plusMinutes(RESET_TOKEN_AVAILABILITY));
            instructorRepository.save(instructor);
            emailService.sendPasswordResetEmail(email, instructor.getFirstName(), code);
            log.info("Reset code generated for instructor: {}", email);
            return;
        }

        // === 3. No user found — silent success (security) ===
        log.warn("Password reset requested for unknown email: {}", email);
        // On ne lève pas d'erreur pour ne pas révéler si l'email existe
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = request.email();
        log.info("Password reset attempt for: {}", email);

        // === 1. Search in Customers ===
        Optional<CustomerEntity> customerOpt = customerRepository.findByEmailAndDeletedFalse(email);
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            validateAndResetPassword(customer.getResetToken(), customer.getResetTokenExpiry(), request.token());
            customer.setPassword(passwordEncoder.encode(request.newPassword()));
            customer.setResetToken(null);
            customer.setResetTokenExpiry(null);
            customerRepository.save(customer);
            log.info("Password successfully reset for customer: {}", email);
            return;
        }

        // === 2. Search in Instructors ===
        Optional<InstructorEntity> instructorOpt = instructorRepository.findByEmailAndDeletedFalse(email);
        if (instructorOpt.isPresent()) {
            InstructorEntity instructor = instructorOpt.get();
            validateAndResetPassword(instructor.getResetToken(), instructor.getResetTokenExpiry(), request.token());
            instructor.setPassword(passwordEncoder.encode(request.newPassword()));
            instructor.setResetToken(null);
            instructor.setResetTokenExpiry(null);
            instructorRepository.save(instructor);
            log.info("Password successfully reset for instructor: {}", email);
            return;
        }

        throw new BadCredentialsException("Utilisateur non trouvé");
    }

    private void validateAndResetPassword(String storedToken, LocalDateTime expiry, String providedToken) {
        if (storedToken == null || expiry == null) {
            throw new BadCredentialsException("Aucune demande de réinitialisation en cours");
        }
        if (expiry.isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Le code a expiré. Veuillez en demander un nouveau.");
        }
        if (!storedToken.equals(providedToken)) {
            throw new BadCredentialsException("Code de vérification incorrect");
        }
    }

}