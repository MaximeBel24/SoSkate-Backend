package com.soskate.api.mappers;

import com.soskate.api.dto.instructor.InstructorSpotResponse;
import com.soskate.api.dto.spot.SpotSummary;
import com.soskate.api.entities.InstructorSpotEntity;
import com.soskate.api.entities.SpotEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstructorSpotMapper {

    public InstructorSpotResponse toResponse(InstructorSpotEntity entity) {
        return new InstructorSpotResponse(
                entity.getId(),
                entity.getInstructor().getId(),
                toSpotSummary(entity.getSpot()),
                entity.getCreatedAt()
        );
    }

    public List<InstructorSpotResponse> toResponseList(List<InstructorSpotEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    private SpotSummary toSpotSummary(SpotEntity entity) {
        return new SpotSummary(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}