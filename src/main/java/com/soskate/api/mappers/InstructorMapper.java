package com.soskate.api.mappers;

import com.soskate.api.dto.instructor.*;
import com.soskate.api.entities.InstructorEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between InstructorEntity and various DTOs.
 */
@Component
public class InstructorMapper {

    public InstructorResponse toResponse(InstructorEntity entity) {
        if (entity == null) {
            return null;
        }

        return new InstructorResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPhone(),
                entity.getStatus(),
                entity.getBio(),
                entity.getSpecialty(),
                entity.getYearsOfExperience(),
                entity.getInstagramHandle(),
                entity.getYoutubeChannel(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getInvitedAt(),
                entity.getActivatedAt()
        );
    }

    /**
     * Converts an InstructorEntity to a summary record for list views.
     */
    public InstructorSummary toSummary(InstructorEntity entity) {
        if (entity == null) {
            return null;
        }

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

    /**
     * Converts a list of InstructorEntity to a list of response records.
     */
    public List<InstructorResponse> toResponseList(List<InstructorEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of InstructorEntity to a list of summary records.
     */
    public List<InstructorSummary> toSummaryList(List<InstructorEntity> entities) {
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new InstructorEntity from a create request.
     * Note: Password, activation token, and status are set by the service.
     */
    public InstructorEntity toEntity(InstructorCreateRequest request) {
        if (request == null) {
            return null;
        }

        return InstructorEntity.builder()
                .email(request.email().toLowerCase().trim())
                .firstname(request.firstname().trim())
                .lastname(request.lastname().trim())
                .phone(request.phone() != null ? request.phone().trim() : null)
                .specialty(request.specialty())
                .yearsOfExperience(request.yearsOfExperience())
                .build();
    }

    /**
     * Updates an existing InstructorEntity with data from an update request.
     * Only non-null fields in the request will update the entity.
     */
    public void updateEntityFromRequest(InstructorEntity entity, InstructorUpdateRequest request) {
        if (request == null) {
            return;
        }

        if (request.firstname() != null) {
            entity.setFirstname(request.firstname().trim());
        }
        if (request.lastname() != null) {
            entity.setLastname(request.lastname().trim());
        }
        if (request.phone() != null) {
            entity.setPhone(request.phone().trim());
        }
        if (request.bio() != null) {
            entity.setBio(request.bio().trim());
        }
        if (request.specialty() != null) {
            entity.setSpecialty(request.specialty());
        }
        if (request.yearsOfExperience() != null) {
            entity.setYearsOfExperience(request.yearsOfExperience());
        }
        if (request.instagramHandle() != null) {
            // Remove @ if user includes it
            String handle = request.instagramHandle().trim();
            if (handle.startsWith("@")) {
                handle = handle.substring(1);
            }
            entity.setInstagramHandle(handle);
        }
        if (request.youtubeChannel() != null) {
            entity.setYoutubeChannel(request.youtubeChannel().trim());
        }
    }
}