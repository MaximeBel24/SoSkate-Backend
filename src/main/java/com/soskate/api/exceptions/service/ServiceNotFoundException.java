package com.soskate.api.exceptions.service;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when a service is not found.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class ServiceNotFoundException extends ApiException {

    public ServiceNotFoundException(Long id) {
        super(ErrorCode.SERVICE_NOT_FOUND, String.format("Service not found with id: %d", id));
    }

    public ServiceNotFoundException(String message) {
        super(ErrorCode.SERVICE_NOT_FOUND, message);
    }
}
