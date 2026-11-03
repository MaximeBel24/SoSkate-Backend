package com.soskate.api.exceptions.spot;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when a spot with the same name and address already exists.
 * 
 * @author SoSkate Team
 * @version 1.0
 */
public class DuplicateSpotException extends ApiException {

    public DuplicateSpotException(String name, String address) {
        super(ErrorCode.SPOT_DUPLICATE, String.format("Spot with name '%s' already exists at address '%s'", name, address));
    }

    public DuplicateSpotException(String message) {
        super(ErrorCode.SPOT_DUPLICATE, message);
    }
}
