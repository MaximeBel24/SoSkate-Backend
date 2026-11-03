package com.soskate.api.exceptions.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Every error the API can report, with the HTTP status it is sent with.
 * <p>
 * The name is the stable {@code code} field of {@link ErrorResponse}: clients may rely on it,
 * so a value must never be renamed once released.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Generic
    INVALID_REQUEST(HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),
    DATA_CONFLICT(HttpStatus.CONFLICT),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),
    PLATFORM_SETTINGS_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication and accounts
    EMAIL_EXISTS(HttpStatus.CONFLICT),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST),

    // Customers
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND),

    // Instructors
    INSTRUCTOR_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_ACCOUNT_STATE(HttpStatus.CONFLICT),
    INSTRUCTOR_ALREADY_DELETED(HttpStatus.CONFLICT),
    INSTRUCTOR_SPOT_ALREADY_EXISTS(HttpStatus.CONFLICT),

    // Availabilities
    AVAILABILITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    AVAILABILITY_OVERLAP(HttpStatus.CONFLICT),

    // Bookings
    BOOKING_ERROR(HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND),
    BOOKING_TOO_SOON(HttpStatus.BAD_REQUEST),
    SLOT_NOT_AVAILABLE(HttpStatus.CONFLICT),
    INSTRUCTOR_NOT_AVAILABLE(HttpStatus.CONFLICT),
    INSTRUCTOR_NOT_AT_SPOT(HttpStatus.BAD_REQUEST),
    CANCELLATION_NOT_ALLOWED(HttpStatus.FORBIDDEN),
    MODIFICATION_NOT_ALLOWED(HttpStatus.FORBIDDEN),
    PARTICIPATION_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT),

    // Photos
    PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_PHOTO_FORMAT(HttpStatus.BAD_REQUEST),
    PHOTO_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    // Services
    SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND),
    SERVICE_EXISTS(HttpStatus.CONFLICT),
    SERVICE_NOT_ACTIVE(HttpStatus.BAD_REQUEST),

    // Spots
    SPOT_NOT_FOUND(HttpStatus.NOT_FOUND),
    SPOT_DUPLICATE(HttpStatus.CONFLICT),
    INVALID_COORDINATES(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;
}
