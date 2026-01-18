package com.soskate.api.services.instructor;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.AvailabilityStatus;

import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.AvailabilityNotFoundException;
import com.soskate.api.exceptions.booking.AvailabilityOverlapException;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.mappers.AvailabilityMapper;
import com.soskate.api.repositories.AvailabilityRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final InstructorRepository instructorRepository;
    private final AvailabilityMapper availabilityMapper;

    @Transactional
    public AvailabilityResponse create(Long instructorId, AvailabilityCreateRequest request) {
        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException("Instructeur non trouvé"));

        if (instructor.getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("L'instructeur doit être actif pour créer une disponibilité");
        }

        // Vérifier pas de chevauchement
        boolean hasOverlap = availabilityRepository.existsOverlapping(
                instructorId,
                request.date(),
                request.startTime(),
                request.endTime()
        );

        if (hasOverlap) {
            throw new AvailabilityOverlapException();
        }

        AvailabilityEntity availability = AvailabilityEntity.builder()
                .instructor(instructor)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AvailabilityStatus.AVAILABLE)
                .build();

        AvailabilityEntity saved = availabilityRepository.save(availability);
        return availabilityMapper.toResponse(saved);
    }

    public List<AvailabilityResponse> getByInstructor(Long instructorId) {
        List<AvailabilityEntity> availabilities = availabilityRepository
                .findByInstructorIdAndStatus(instructorId, AvailabilityStatus.AVAILABLE);
        return availabilityMapper.toResponseList(availabilities);
    }

    public List<AvailabilityResponse> getByInstructorAndDateRange(
            Long instructorId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<AvailabilityEntity> availabilities = availabilityRepository
                .findAvailableByInstructorAndDateRange(instructorId, startDate, endDate);
        return availabilityMapper.toResponseList(availabilities);
    }

    public AvailabilityResponse getById(Long id) {
        AvailabilityEntity availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Disponibilité non trouvée"));
        return availabilityMapper.toResponse(availability);
    }

    @Transactional
    public AvailabilityResponse update(Long instructorId, Long id, AvailabilityUpdateRequest request) {
        AvailabilityEntity availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Disponibilité non trouvée"));

        if (!availability.getInstructor().getId().equals(instructorId)) {
            throw new AvailabilityNotFoundException("Disponibilité non trouvée");
        }

        if (request.date() != null) {
            availability.setDate(request.date());
        }
        if (request.startTime() != null) {
            availability.setStartTime(request.startTime());
        }
        if (request.endTime() != null) {
            availability.setEndTime(request.endTime());
        }

        AvailabilityEntity saved = availabilityRepository.save(availability);
        return availabilityMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long instructorId, Long id) {
        AvailabilityEntity availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Disponibilité non trouvée"));

        if (!availability.getInstructor().getId().equals(instructorId)) {
            throw new AvailabilityNotFoundException("Disponibilité non trouvée");
        }

        availability.setStatus(AvailabilityStatus.CANCELLED);
        availabilityRepository.save(availability);
    }
}