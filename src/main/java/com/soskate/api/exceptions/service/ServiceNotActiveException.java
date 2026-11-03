package com.soskate.api.exceptions.service;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when attempting to use a deactivated service.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class ServiceNotActiveException extends ApiException {

    public ServiceNotActiveException(Long id) {
        super(ErrorCode.SERVICE_NOT_ACTIVE, String.format("Service with id %d is disabled and cannot be used", id));
    }

    public ServiceNotActiveException(String serviceName) {
        super(ErrorCode.SERVICE_NOT_ACTIVE, String.format("Service '%s' is disabled and cannot be used", serviceName));
    }
}
