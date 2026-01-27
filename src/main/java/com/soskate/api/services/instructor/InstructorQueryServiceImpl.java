package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.dto.instructor.InstructorUpdateRequest;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.SkateSpecialty;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.mappers.InstructorMapper;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of InstructorQueryService.
 * Handles instructor read operations and profile updates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorQueryServiceImpl implements InstructorQueryService {

    private final InstructorRepository instructorRepository;
    private final InstructorMapper instructorMapper;

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
     * Updates an instructor's profile.
     */
    @Transactional
    public InstructorResponse updateProfile(Long instructorId, InstructorUpdateRequest request) {
        log.info("Updating profile for instructor id: {}", instructorId);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));

        instructorMapper.updateEntityFromRequest(instructor, request);
        instructor.setUpdatedAt(LocalDateTime.now());

        InstructorEntity savedInstructor = instructorRepository.save(instructor);
        log.info("Profile updated for instructor id: {}", instructorId);

        return instructorMapper.toResponse(savedInstructor);
    }
}
