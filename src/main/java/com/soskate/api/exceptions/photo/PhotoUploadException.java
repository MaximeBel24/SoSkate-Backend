package com.soskate.api.exceptions.photo;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when photo upload fails.
 */
public class PhotoUploadException extends ApiException {

    public PhotoUploadException(String message) {
        super(ErrorCode.PHOTO_UPLOAD_ERROR, message);
    }

    public PhotoUploadException(String message, Throwable cause) {
        super(ErrorCode.PHOTO_UPLOAD_ERROR, message, cause);
    }
}
