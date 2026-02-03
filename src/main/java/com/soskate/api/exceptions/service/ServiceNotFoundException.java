package com.soskate.api.exceptions.service;

/**
 * Exception levée lorsqu'un service est introuvable.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(Long id) {
        super(String.format("Service not found with id: %d", id));
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }
}