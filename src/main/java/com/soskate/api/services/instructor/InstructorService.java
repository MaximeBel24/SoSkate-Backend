package com.soskate.api.services.instructor;

import com.soskate.api.config.SoskateSecurityProperties;
import com.soskate.api.dto.instructor.*;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.SkateSpecialty;

import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.exceptions.auth.PasswordMismatchException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.auth.InvalidActivationTokenException;
import com.soskate.api.mappers.InstructorMapper;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.services.email.EmailService;
import com.soskate.api.services.token.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service handling all instructor-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;
    private final TokenGeneratorService tokenGeneratorService;
    private final EmailService emailService;
//    private final PasswordEncoder passwordEncoder;
    private final SoskateSecurityProperties securityProperties;

    // ==================== Admin Operations ====================

    /**
     * Creates a new instructor account and sends an invitation email.
     * Called by admins only.
     *
     * @param request The instructor creation data
     * @return The created instructor's data
     */
    @Transactional
    public InstructorResponse createInstructor(InstructorCreateRequest request) {
        log.info("Creating new instructor with email: {}", request.email());

        // Check if email already exists
        String normalizedEmail = request.email().toLowerCase().trim();
        if (instructorRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        // Create entity from request
        InstructorEntity instructor = instructorMapper.toEntity(request);

        // Generate temporary password
//        String temporaryPassword = tokenGeneratorService.generateSecurePassword(
//                securityProperties.getGeneratedPasswordLength()
//        );
        // TODO : Implement spring security to encode the password
//        instructor.setPassword(passwordEncoder.encode(temporaryPassword));
//        instructor.setPassword(temporaryPassword);
        instructor.setPassword("PENDING_ACTIVATION");

        // Generate activation token
        String activationToken = tokenGeneratorService.generateActivationToken();
        instructor.setActivationToken(activationToken);
        instructor.setActivationTokenExpiry(
                LocalDateTime.now().plusHours(securityProperties.getInstructorActivationTokenTtlHours())
        );

        // Set initial status and audit fields
        instructor.setStatus(InstructorStatus.INVITED);
        instructor.setInvitedAt(LocalDateTime.now());
        instructor.setCreatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        // Save instructor
        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor created with id: {}", savedInstructor.getId());

        // Send invitation email
        emailService.sendInstructorInvitation(savedInstructor);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Resends the invitation to an instructor with a new token.
     * Called by admins when the original invitation expired or wasn't received.
     *
     * @param instructorId The instructor's ID
     * @return The updated instructor's data
     */
    @Transactional
    public InstructorResponse resendInvitation(Long instructorId) {
        log.info("Resending invitation for instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        // Only resend if instructor is still in INVITED status
        if (instructor.getStatus() != InstructorStatus.INVITED) {
            throw new IllegalStateException("Cannot resend invitation to an already activated account");
        }

        // Generate new temporary password
//        String newTemporaryPassword = tokenGeneratorService.generateSecurePassword(
//                securityProperties.getGeneratedPasswordLength()
//        );
//        instructor.setPassword(passwordEncoder.encode(temporaryPassword));
//        instructor.setPassword(newTemporaryPassword);
        instructor.setPassword("PENDING_REACTIVATION");

        // Generate new activation token (invalidates the old one)
        String newActivationToken = tokenGeneratorService.generateActivationToken();
        instructor.setActivationToken(newActivationToken);
        instructor.setActivationTokenExpiry(
                LocalDateTime.now().plusHours(securityProperties.getInstructorActivationTokenTtlHours())
        );

        // Update audit field
        instructor.setInvitedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        // Save and send email
        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        emailService.sendInvitationResent(savedInstructor);

        log.info("Invitation resent for instructor id: {}", instructorId);
        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Suspends an instructor account.
     *
     * @param instructorId The instructor's ID
     * @return The updated instructor's data
     */
    @Transactional
    public InstructorResponse suspendInstructor(Long instructorId) {
        log.info("Suspending instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        instructor.setStatus(InstructorStatus.SUSPENDED);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor {} suspended", instructorId);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Reactivates a suspended instructor account.
     *
     * @param instructorId The instructor's ID
     * @return The updated instructor's data
     */
    @Transactional
    public InstructorResponse reactivateInstructor(Long instructorId) {
        log.info("Reactivating instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        if (instructor.getStatus() != InstructorStatus.SUSPENDED) {
            throw new IllegalStateException("Can only reactivate suspended accounts");
        }

        instructor.setStatus(InstructorStatus.ACTIVE);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor {} reactivated", instructorId);

        return instructorMapper.toResponse(savedInstructor);
    }

    // ==================== Activation ====================

    /**
     * Activates an instructor account using the activation token.
     * Called by the instructor when they click the activation link.
     *
     * @param request The activation data (token + new password)
     * @return The activated instructor's data
     */
    @Transactional
    public InstructorResponse activateAccount(InstructorActivateRequest request) {
        log.info("Activating instructor account with token");

        // Validate password confirmation
        if (!request.password().equals(request.passwordConfirmation())) {
            throw new PasswordMismatchException();
        }

        // Find instructor by token
        InstructorEntity instructor = instructorRepository.findByActivationToken(request.token())
                .orElseThrow(InvalidActivationTokenException::new);

        // Check if token is still valid
        if (!instructor.isActivationTokenValid()) {
            throw new InvalidActivationTokenException("Activation token has expired");
        }

        // Check if already activated
        if (instructor.getStatus() != InstructorStatus.INVITED) {
            throw new IllegalStateException("Account has already been activated");
        }

        // Update password with the one chosen by the instructor
        // TODO : Implement spring security to encode the password
//        instructor.setPassword(passwordEncoder.encode(temporaryPassword));
        instructor.setPassword(request.password());

        // Clear activation token (no longer needed)
        instructor.setActivationToken(null);
        instructor.setActivationTokenExpiry(null);

        // Update status and audit fields
        instructor.setStatus(InstructorStatus.ACTIVE);
        instructor.setActivatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        // Save
        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor account activated: {}", savedInstructor.getId());

        // Send confirmation email
        emailService.sendActivationConfirmation(savedInstructor);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Validates an activation token without activating.
     * Used to check if a token is valid before showing the activation form.
     *
     * @param token The activation token
     * @return true if the token is valid
     */
    @Transactional(readOnly = true)
    public boolean validateActivationToken(String token) {
        return instructorRepository.findByActivationToken(token)
                .map(InstructorEntity::isActivationTokenValid)
                .orElse(false);
    }

    // ==================== Profile Operations ====================

    /**
     * Updates an instructor's profile.
     * Called by the instructor to complete/update their own profile.
     *
     * @param instructorId The instructor's ID
     * @param request      The update data
     * @return The updated instructor's data
     */
    @Transactional
    public InstructorResponse updateProfile(Long instructorId, InstructorUpdateRequest request) {
        log.info("Updating profile for instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        // Apply updates
        instructorMapper.updateEntityFromRequest(instructor, request);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Profile updated for instructor id: {}", instructorId);

        return instructorMapper.toResponse(savedInstructor);
    }

    // ==================== Read Operations ====================

    /**
     * Gets an instructor by ID.
     */
    @Transactional(readOnly = true)
    public InstructorResponse getInstructorById(Long instructorId) {
        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));
        return instructorMapper.toResponse(instructor);
    }

    /**
     * Gets all instructors (for admin).
     */
    @Transactional(readOnly = true)
    public List<InstructorResponse> getAllInstructors() {
        return instructorMapper.toResponseList(instructorRepository.findAll());
    }

    /**
     * Gets all active instructors (for public listing).
     */
    @Transactional(readOnly = true)
    public List<InstructorSummary> getActiveInstructors() {
        return instructorMapper.toSummaryList(instructorRepository.findAllActive());
    }

    /**
     * Gets instructors by specialty.
     */
    @Transactional(readOnly = true)
    public List<InstructorSummary> getInstructorsBySpecialty(SkateSpecialty specialty) {
        return instructorMapper.toSummaryList(
                instructorRepository.findActiveBySpecialty(specialty)
        );
    }

    /**
     * Searches instructors by name.
     */
    @Transactional(readOnly = true)
    public List<InstructorSummary> searchInstructors(String query) {
        return instructorMapper.toSummaryList(
                instructorRepository.searchActiveByName(query)
        );
    }

    /**
     * Gets instructors with pending invitations (for admin).
     */
    @Transactional(readOnly = true)
    public List<InstructorResponse> getPendingInvitations() {
        return instructorMapper.toResponseList(instructorRepository.findAllInvited());
    }

    /**
     * Gets instructors with expired invitations (for admin cleanup).
     */
    @Transactional(readOnly = true)
    public List<InstructorResponse> getExpiredInvitations() {
        return instructorMapper.toResponseList(instructorRepository.findExpiredInvitations());
    }

    // ==================== Delete Operations ====================

    /**
     * Deletes an instructor (soft delete recommended in production).
     *
     * @param instructorId The instructor's ID
     */
    @Transactional
    public void deleteInstructor(Long instructorId) {
        log.info("Deleting instructor id: {}", instructorId);

        if (!instructorRepository.existsById(instructorId)) {
            throw new InstructorNotFoundException(instructorId);
        }

        // TODO: Consider soft delete instead
        // TODO: Handle related bookings, events, etc.
        instructorRepository.deleteById(instructorId);

        log.info("Instructor {} deleted", instructorId);
    }
}