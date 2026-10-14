package com.soskate.api.exceptions.service;

import com.soskate.api.enums.ServiceType;
import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;
import lombok.Getter;

/**
 * Exception thrown when a service with the same name already exists.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Getter
public class ServiceAlreadyExistsException extends ApiException {

    public ServiceAlreadyExistsException(String name) {
        super(ErrorCode.SERVICE_EXISTS, String.format("Service with %s already exists", name));
    }
    public ServiceAlreadyExistsException(String name, boolean withSuggestion) {
        super(ErrorCode.SERVICE_EXISTS, String.format("Service with name '%s' already exists. Please choose another name.", name));
    }

}
