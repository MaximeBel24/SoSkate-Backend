package com.soskate.api.exceptions.service;

/**
 * Exception thrown when attempting to use a deactivated service.
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