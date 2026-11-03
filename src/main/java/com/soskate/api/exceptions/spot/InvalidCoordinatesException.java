package com.soskate.api.exceptions.spot;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when GPS coordinates are invalid.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class InvalidCoordinatesException extends ApiException {

    public InvalidCoordinatesException(String message) {
        super(ErrorCode.INVALID_COORDINATES, message);
    }

    public InvalidCoordinatesException() {
        super(ErrorCode.INVALID_COORDINATES, "Invalid GPS coordinates provided");
    }
}
