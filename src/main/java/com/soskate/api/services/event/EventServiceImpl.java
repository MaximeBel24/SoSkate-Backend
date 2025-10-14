package com.soskate.api.services.event;

import com.soskate.api.dtos.event.EventRequestDto;
import com.soskate.api.dtos.event.EventResponseDto;
import com.soskate.api.exceptions.event.EventDataRetrievalException;
import com.soskate.api.mappers.EventMapper;
import com.soskate.api.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{

    private final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEvents() {
        log.info("Invoking getAllEvents");
        try {

        } catch (DataAccessException e) {
            log.error("Database error while retrieving all events", e);
            throw EventDataRetrievalException
        }
        return List.of();
    }

    @Override
    public EventResponseDto getEventById(Long id) {
        return null;
    }

    @Override
    public EventResponseDto createEvent(EventRequestDto eventToCreate) {
        return null;
    }

    @Override
    public EventResponseDto updateEvent(Long id, EventRequestDto eventToUpdate) {
        return null;
    }

    @Override
    public void deleteEvent(Long id) {

    }
}
