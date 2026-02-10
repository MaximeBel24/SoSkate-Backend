package com.soskate.api.exceptions.service;

import com.soskate.api.enums.ServiceType;
import lombok.Getter;

/**
 * Exception thrown when a service with the same name already exists.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Getter
public class ServiceAlreadyExistsException extends RuntimeException {

    public ServiceAlreadyExistsException(String name) {
        super(String.format("Service with %s already exists", name));
    }
    public ServiceAlreadyExistsException(String name, boolean withSuggestion) {
        super(String.format("Service with name '%s' already exists. Please choose another name.", name));
    }

}