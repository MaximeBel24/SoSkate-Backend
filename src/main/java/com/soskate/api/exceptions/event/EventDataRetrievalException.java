package com.soskate.api.exceptions.event;

import org.springframework.dao.DataAccessException;

public class EventDataRetrievalException extends RuntimeException{
    public EventDataRetrievalException(String message) {super(message);}
    public EventDataRetrievalException(String message, Throwable cause) {super(message, cause);}
    public EventDataRetrievalException(DataAccessException cause) {super("Database access ");}


}
