package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorActivateRequest;
import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.auth.InvalidActivationTokenException;
import com.soskate.api.exceptions.auth.PasswordMismatchException;
import com.soskate.api.exceptions.instructor.InvalidAccountStateException;
import com.soskate.api.mappers.InstructorMapper;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of InstructorActivationService.
 * Handles token validation and account activation flow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorActivationServiceImpl implements InstructorActivationService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;
    private final EmailService emailService;

    /**
     * Activates an instructor account using the activation token.
     */
    @Transactional
    public InstructorResponse activateAccount(InstructorActivateRequest request) {
        log.info("Activating instructor account with token");

        if (!request.password().equals(request.passwordConfirmation())) {
            throw new PasswordMismatchException();
        }

        InstructorEntity instructor = instructorRepository.findByActivationToken(request.token())
                .orElseThrow(InvalidActivationTokenException::new);

        if (!instructor.isActivationTokenValid()) {
            throw new InvalidActivationTokenException("Activation token has expired");
        }

        if (instructor.getStatus() != InstructorStatus.INVITED) {
            throw new InvalidAccountStateException("Account has already been activated");
        }

        // TODO: Implement spring security to encode the password
        instructor.setPassword(request.password());

        instructor.setActivationToken(null);
        instructor.setActivationTokenExpiry(null);

        instructor.setStatus(InstructorStatus.ACTIVE);
        instructor.setActivatedAt(LocalDateTime.now());
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Instructor account activated: {}", savedInstructor.getId());

        emailService.sendActivationConfirmation(savedInstructor);

        return instructorMapper.toResponse(savedInstructor);
    }

    /**
     * Validates an activation token without activating.
     * Used to check if a token is valid before showing the activation form.
     */
    @Transactional(readOnly = true)
    public boolean validateActivationToken(String token) {
        return instructorRepository.findByActivationToken(token)
                .map(InstructorEntity::isActivationTokenValid)
                .orElse(false);
    }
}
