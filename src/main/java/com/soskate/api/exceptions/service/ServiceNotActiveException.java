package com.soskate.api.exceptions.service;

/**
 * Exception levée lorsqu'on tente d'utiliser un service désactivé.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class ServiceNotActiveException extends RuntimeException {

    public ServiceNotActiveException(Long id) {
        super(String.format("Le service avec l'ID %d est désactivé et ne peut pas être utilisé", id));
    }

    public ServiceNotActiveException(String serviceName) {
        super(String.format("Le service '%s' est désactivé et ne peut pas être utilisé", serviceName));
    }
}