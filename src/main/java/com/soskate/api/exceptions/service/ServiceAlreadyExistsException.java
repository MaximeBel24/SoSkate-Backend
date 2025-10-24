package com.soskate.api.exceptions.service;

import com.soskate.api.enums.ServiceType;
import lombok.Getter;

/**
 * Exception levée lorsqu'un service avec le même nom existe déjà.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Getter
public class ServiceAlreadyExistsException extends RuntimeException {

    public ServiceAlreadyExistsException(String name) {
        super(String.format("Un service avec le nom '%s' existe déjà", name));
    }
    public ServiceAlreadyExistsException(String name, boolean withSuggestion) {
        super(String.format("Un service avec le nom '%s' existe déjà. Veuillez choisir un autre nom.", name));
    }

}