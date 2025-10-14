package com.soskate.api.exceptions.service;

import lombok.Getter;

/**
 * Exception lancée quand un service demandé n'existe pas
 */
@Getter
public class ServiceNotFoundException extends RuntimeException {

    /**
     * -- GETTER --
     *
     * @return l'ID du service qui n'a pas été trouvé
     */
    private final Long serviceId;

    public ServiceNotFoundException(Long id) {
        super("Service with id " + id + " could not be found.");
        this.serviceId = id;
    }

}