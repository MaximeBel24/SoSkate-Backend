package com.soskate.api.services.event;


import com.soskate.api.dtos.event.EventRequestDto;
import com.soskate.api.dtos.event.EventResponseDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des événements
 */
public interface EventService {

    /**
     * Récupère tous les événements actifs
     * @return Liste de tous les événements actifs
     */
    List<EventResponseDto> getAllEvents();

    /**
     * Récupère un événement par son ID
     * @param id L'identifiant de l'événement
     * @return L'événement correspondant
     */
    EventResponseDto getEventById(Long id);

    /**
     * Crée un nouvel événement
     * @param eventToCreate Les données de l'événement à créer
     * @return L'événement créé
     */
    EventResponseDto createEvent(EventRequestDto eventToCreate);

    /**
     * Met à jour un événement existant
     * @param id L'identifiant de l'événement à modifier
     * @param eventToUpdate Les nouvelles données de l'événement
     * @return L'événement mis à jour
     */
    EventResponseDto updateEvent(Long id, EventRequestDto eventToUpdate);

    /**
     * Supprime un événement (désactivation logique)
     * @param id L'identifiant de l'événement à supprimer
     */
    void deleteEvent(Long id);

}