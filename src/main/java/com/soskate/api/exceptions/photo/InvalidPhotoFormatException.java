package com.soskate.api.exceptions.photo;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

/**
 * Exception thrown when uploaded file format is invalid or unsupported.
 */
public class InvalidPhotoFormatException extends ApiException {

    public InvalidPhotoFormatException(String message) {
        super(ErrorCode.INVALID_PHOTO_FORMAT, message);
    }

    public InvalidPhotoFormatException(String message, Throwable cause) {
        super(ErrorCode.INVALID_PHOTO_FORMAT, message, cause);
    }
}
