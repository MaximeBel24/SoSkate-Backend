package com.soskate.api.exceptions.service;

/**
 * Exception levée lorsqu'on tente d'utiliser un service désactivé.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class ServiceNotActiveException extends RuntimeException {

    public ServiceNotActiveException(Long id) {
        super(String.format("Service with id %d is disabled and cannot be used", id));
    }

    public ServiceNotActiveException(String serviceName) {
        super(String.format("Service '%s' is disabled and cannot be used", serviceName));
    }
}