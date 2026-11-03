package com.soskate.api.exceptions.photo;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when a requested photo is not found.
 */
public class PhotoNotFoundException extends ApiException {

    public PhotoNotFoundException(Long photoId) {
        super(ErrorCode.PHOTO_NOT_FOUND, "Photo not found with ID: " + photoId);
    }

    public PhotoNotFoundException(String message) {
        super(ErrorCode.PHOTO_NOT_FOUND, message);
    }
}
