package com.soskate.api.exceptions.service;

import com.soskate.api.enums.ServiceType;
import lombok.Getter;

/**
 * Exception lancée quand on tente de créer/modifier un service qui existe déjà
 * Basée sur l'unicité nom + type de service
 */
@Getter
public class ServiceAlreadyExistsException extends RuntimeException {

    /**
     * -- GETTER --
     *
     * @return le nom du service en conflit
     */
    private final String serviceName;
    /**
     * -- GETTER --
     *
     * @return le type du service en conflit
     */
    private final ServiceType serviceType;

    public ServiceAlreadyExistsException(String name, ServiceType type) {
        super("Service with name : " + name + " and service type : " + type + " already exists.");
        this.serviceName = name;
        this.serviceType = type;
    }

}