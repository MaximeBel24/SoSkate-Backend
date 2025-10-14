package com.soskate.api.mappers;

import com.soskate.api.dtos.event.EventResponseDto;
import com.soskate.api.entities.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDto eventToEventDto(EventEntity event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getMaxParticipants(),
                event.getIsActive(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
