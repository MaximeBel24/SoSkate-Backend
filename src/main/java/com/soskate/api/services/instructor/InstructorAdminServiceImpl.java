package com.soskate.api.services.instructor;

import com.soskate.api.config.SoskateSecurityProperties;
import com.soskate.api.dto.instructor.InstructorCreateRequest;
import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.instructor.InstructorAlreadyDeletedException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.instructor.InvalidAccountStateException;
import com.soskate.api.mappers.InstructorMapper;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.services.common.EmailValidationService;
import com.soskate.api.services.email.EmailService;
import com.soskate.api.services.token.TokenGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of InstructorAdminService.
 * Handles creation, invitation, suspension, and deletion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorAdminServiceImpl implements InstructorAdminService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;
    private final EmailValidationService emailValidationService;
    private final TokenGeneratorService tokenGeneratorService;
    private final EmailService emailService;
    private final SoskateSecurityProperties securityProperties;

    /**
     * Creates a new instructor account and sends an invitation email.
     */
    @Transactional
    public InstructorResponse createInstructor(InstructorCreateRequest request) {
        log.info("Creating new instructor with email: {}", request.email());

        emailValidationService.validateEmailUnique(request.email());

        InstructorEntity instructor = instructorMapper.toEntity(request);

        // TODO: Implement spring security to encode the password
        instructor.setPassword("PENDING_ACTIVATION");

        String activationToken = tokenGeneratorService.generateActivationToken();
        instructor.setActivationToken(activationToken);
        instructor.setActivationTokenExpiry(
                LocalDateTime.now().plusHours(securityProperties.getInstructorActivationTokenTtlHours())
        );

        instructor.setStatus(InstructorStatus.INVITED);
        instructor.setInvitedAt(LocalDateTime.now());
        instructor.setCreatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor created with id: {}", savedInstructor.getId());

        emailService.sendInstructorInvitation(savedInstructor);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Resends the invitation to an instructor with a new token.
     */
    @Transactional
    public InstructorResponse resendInvitation(Long instructorId) {
        log.info("Resending invitation for instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        if (instructor.getStatus() != InstructorStatus.INVITED) {
            throw new InvalidAccountStateException("Cannot resend invitation to an already activated account");
        }

        instructor.setPassword("PENDING_REACTIVATION");

        String newActivationToken = tokenGeneratorService.generateActivationToken();
        instructor.setActivationToken(newActivationToken);
        instructor.setActivationTokenExpiry(
                LocalDateTime.now().plusHours(securityProperties.getInstructorActivationTokenTtlHours())
        );

        instructor.setInvitedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        emailService.sendInvitationResent(savedInstructor);

        log.info("Invitation resent for instructor id: {}", instructorId);
        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Suspends an instructor account.
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
     */
    @Transactional
    public InstructorResponse reactivateInstructor(Long instructorId) {
        log.info("Reactivating instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        if (instructor.getStatus() != InstructorStatus.SUSPENDED) {
            throw new InvalidAccountStateException("Can only reactivate suspended accounts");
        }

        instructor.setStatus(InstructorStatus.ACTIVE);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor {} reactivated", instructorId);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Deletes an instructor.
     */
    @Transactional
    public void deleteInstructorById(Long instructorId) {
        log.info("Deleting instructor id: {}", instructorId);

        if (!instructorRepository.existsById(instructorId)) {
            throw new InstructorNotFoundException(instructorId);
        }

        InstructorEntity instructor = instructorRepository.getReferenceById(instructorId);
        if (Boolean.TRUE.equals(instructor.getDeleted())) {
            throw new InstructorAlreadyDeletedException("Cet instructeur est déjà supprimé");
        }

        instructor.markAsDeleted();

        instructorRepository.save(instructor);

        log.info("Instructor {} deleted", instructorId);
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
}
