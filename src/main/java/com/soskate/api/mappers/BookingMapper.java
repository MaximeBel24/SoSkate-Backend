package com.soskate.api.mappers;

import com.soskate.api.dto.booking.*;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.dto.service.ServiceSummary;
import com.soskate.api.dto.spot.SpotSummary;
import com.soskate.api.entities.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponse toResponse(BookingEntity entity) {
        int confirmedParticipants = entity.getParticipants().stream()
                .filter(BookingParticipantEntity::isConfirmed)
                .mapToInt(BookingParticipantEntity::getNumberOfParticipants)
                .sum();

        return new BookingResponse(
                entity.getId(),
                toInstructorSummary(entity.getInstructor()),
                toSpotSummary(entity.getSpot()),
                toServiceSummary(entity.getService()),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDurationMinutes(),
                entity.getMaxParticipants(),
                confirmedParticipants,
                entity.getMaxParticipants() - confirmedParticipants,
                entity.getStatus(),
                entity.getNotes(),
                entity.getParticipants().stream()
                        .map(this::toParticipantSummary)
                        .toList(),
                entity.getCreatedAt()
        );
    }

    public List<BookingResponse> toResponseList(List<BookingEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public InstructorSummary toInstructorSummary(InstructorEntity entity) {
        return new InstructorSummary(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getSpecialty(),
                entity.getYearsOfExperience()
        );
    }

    public SpotSummary toSpotSummary(SpotEntity entity) {
        return new SpotSummary(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }

    public ServiceSummary toServiceSummary(ServiceEntity entity) {
        return new ServiceSummary(
                entity.getId(),
                entity.getName(),
                entity.getBasePriceCents(),
                entity.getMaxParticipants()
        );
    }

    public ParticipantSummary toParticipantSummary(BookingParticipantEntity entity) {
        return new ParticipantSummary(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getCustomer().getFirstname() + " " + entity.getCustomer().getLastname(),
                entity.getNumberOfParticipants(),
                entity.getStatus(),
                entity.getAmountCents(),
                entity.getInvitedBy(),
                entity.getParticipantsNotes()
        );
    }
}