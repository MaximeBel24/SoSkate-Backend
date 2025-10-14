package com.soskate.api.exceptions.user;

import org.springframework.dao.DataAccessException;

public class UserDataRetrievalException extends RuntimeException {
    public UserDataRetrievalException(Exception e) {
        super("Could not retrieve user data", e);
    }
}
