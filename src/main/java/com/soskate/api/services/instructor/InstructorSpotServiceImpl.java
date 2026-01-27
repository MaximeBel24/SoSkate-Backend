package com.soskate.api.services.instructor;

import com.soskate.api.dto.instructor.InstructorSpotRequest;
import com.soskate.api.dto.instructor.InstructorSpotResponse;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.InstructorSpotEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.common.ResourceNotFoundException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.mappers.InstructorMapper;
import com.soskate.api.mappers.InstructorSpotMapper;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.repositories.InstructorSpotRepository;
import com.soskate.api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of InstructorSpotService.
 * Manages instructor-spot associations.
 */
@Service
@RequiredArgsConstructor
public class InstructorSpotServiceImpl implements InstructorSpotService {

    private final InstructorSpotRepository instructorSpotRepository;
    private final InstructorRepository instructorRepository;
    private final SpotRepository spotRepository;
    private final InstructorSpotMapper instructorSpotMapper;
    private final InstructorMapper instructorMapper;

    @Transactional
    public InstructorSpotResponse addSpotToInstructor(Long instructorId, InstructorSpotRequest request) {
        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructeur non trouvé"));

        if (instructor.getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("L'instructeur doit être actif pour ajouter un spot");
        }

        SpotEntity spot = spotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResourceNotFoundException("Spot non trouvé"));

        if (instructorSpotRepository.existsByInstructorIdAndSpotId(instructorId, request.spotId())) {
            throw new IllegalStateException("L'instructeur enseigne déjà sur ce spot");
        }

        InstructorSpotEntity instructorSpot = InstructorSpotEntity.builder()
                .instructor(instructor)
                .spot(spot)
                .build();

        InstructorSpotEntity saved = instructorSpotRepository.save(instructorSpot);
        return instructorSpotMapper.toResponse(saved);
    }

    public List<InstructorSpotResponse> getSpotsByInstructor(Long instructorId) {
        List<InstructorSpotEntity> instructorSpots = instructorSpotRepository.findByInstructorId(instructorId);
        return instructorSpotMapper.toResponseList(instructorSpots);
    }

    public List<InstructorSummary> getInstructorsBySpot(Long spotId) {
        List<InstructorEntity> instructors = instructorSpotRepository.findActiveInstructorsBySpotId(spotId);

        return instructors.stream()
                .map(instructorMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeSpotFromInstructor(Long instructorId, Long spotId) {
        if (!instructorSpotRepository.existsByInstructorIdAndSpotId(instructorId, spotId)) {
            throw new ResourceNotFoundException("Association non trouvée");
        }
        instructorSpotRepository.deleteByInstructorIdAndSpotId(instructorId, spotId);
    }

    public boolean instructorTeachesAtSpot(Long instructorId, Long spotId) {
        return instructorSpotRepository.existsByInstructorIdAndSpotId(instructorId, spotId);
    }
}