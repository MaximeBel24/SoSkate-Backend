package com.soskate.api.exceptions.spot;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when a spot is not found.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class SpotNotFoundException extends ApiException {

    public SpotNotFoundException(Long id) {
        super(ErrorCode.SPOT_NOT_FOUND, String.format("Spot not found with id: %d", id));
    }

    public SpotNotFoundException(String message) {
        super(ErrorCode.SPOT_NOT_FOUND, message);
    }
}
