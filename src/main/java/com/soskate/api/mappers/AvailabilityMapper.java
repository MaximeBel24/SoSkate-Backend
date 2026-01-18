package com.soskate.api.mappers;

import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.entities.AvailabilityEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailabilityMapper {

    public AvailabilityResponse toResponse(AvailabilityEntity entity) {
        return new AvailabilityResponse(
                entity.getId(),
                entity.getInstructor().getId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public List<AvailabilityResponse> toResponseList(List<AvailabilityEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}