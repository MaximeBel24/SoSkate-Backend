package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;
import com.soskate.api.exceptions.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The single place where exceptions raised while handling a request become HTTP responses.
 * <p>
 * Every response uses the {@link ErrorResponse} format. Messages of 5xx errors are never sent
 * to the client: they may contain internal details, so they are only logged.
 * <p>
 * Errors raised in the security filter chain (missing or invalid authentication) do not reach
 * this class: see {@code CustomAuthenticationEntryPoint} and {@code CustomAccessDeniedHandler}.
 */
@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    static final String INTERNAL_ERROR_MESSAGE = "An internal error occurred";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorCode code = ex.getCode();
        if (code.getStatus().is5xxServerError()) {
            log.error("{}: {}", code, ex.getMessage(), ex);
            return respond(code, INTERNAL_ERROR_MESSAGE);
        }
        log.warn("{}: {}", code, ex.getMessage());
        return respond(code, ex.getMessage());
    }

    /**
     * Raised by method security ({@code @PreAuthorize}) once the user is authenticated.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return respond(ErrorCode.ACCESS_DENIED, "Access denied");
    }

    /**
     * A database constraint rejected the change, typically a unique key hit by two concurrent requests.
     * The database message names tables and keys, so it is not sent to the client.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return respond(ErrorCode.DATA_CONFLICT, "The request conflicts with existing data");
    }

    /**
     * Safety net for argument checks that libraries apply to request input, such as paging or sorting.
     * The message comes from library code, so it is not sent to the client.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return respond(ErrorCode.INVALID_REQUEST, "Invalid request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return respond(ErrorCode.INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()
                .forEach(error -> errors.putIfAbsent(error.getObjectName(), error.getDefaultMessage()));

        log.warn("Validation failed on: {}", errors.keySet());
        ErrorCode code = ErrorCode.VALIDATION_FAILED;
        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, "Invalid request data", errors));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.warn("Unreadable request body: {}", ex.getMessage());
        ErrorCode code = ErrorCode.INVALID_REQUEST;
        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, describeUnreadableBody(ex)));
    }

    /**
     * Every other Spring MVC exception (missing parameter, wrong type, unsupported method or media type,
     * file too large...) ends up here with the status Spring chose for it.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        ErrorCode code = toErrorCode(statusCode);
        String message;
        if (code.getStatus().is5xxServerError()) {
            log.error("Request failed with status {}", statusCode.value(), ex);
            message = INTERNAL_ERROR_MESSAGE;
        } else {
            log.warn("Request rejected with status {}: {}", statusCode.value(), ex.getMessage());
            // Most handlers of the parent class pass a null body: the detail is held by the exception itself.
            Object problem = body == null && ex instanceof org.springframework.web.ErrorResponse springError
                    ? springError.getBody()
                    : body;
            message = detailOf(problem, code.getStatus());
        }
        return ResponseEntity.status(code.getStatus())
                .headers(headers)
                .body(ErrorResponse.of(code, message));
    }

    private static ResponseEntity<ErrorResponse> respond(ErrorCode code, String message) {
        return ResponseEntity.status(code.getStatus()).body(ErrorResponse.of(code, message));
    }

    private static ErrorCode toErrorCode(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 400 -> ErrorCode.INVALID_REQUEST;
            case 404 -> ErrorCode.RESOURCE_NOT_FOUND;
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            case 406 -> ErrorCode.NOT_ACCEPTABLE;
            case 413 -> ErrorCode.PAYLOAD_TOO_LARGE;
            case 415 -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> statusCode.is4xxClientError() ? ErrorCode.INVALID_REQUEST : ErrorCode.INTERNAL_ERROR;
        };
    }

    private static String detailOf(Object body, HttpStatus fallbackStatus) {
        if (body instanceof ProblemDetail problemDetail && problemDetail.getDetail() != null) {
            return problemDetail.getDetail();
        }
        return fallbackStatus.getReasonPhrase();
    }

    private static String describeUnreadableBody(HttpMessageNotReadableException ex) {
        // The direct cause is Jackson's exception, whose message names the target type (e.g. LocalDate);
        // the root cause would only hold the raw parsing error.
        Throwable cause = ex.getCause();
        String causeMessage = cause == null || cause.getMessage() == null ? "" : cause.getMessage();
        if (causeMessage.contains("LocalDate")) {
            return "Invalid date format. Use format: yyyy-MM-dd (e.g. 1995-06-15)";
        }
        if (causeMessage.contains("JSON")) {
            return "Invalid JSON. Check the syntax (commas, quotes, braces)";
        }
        return "Invalid request format. Check the JSON payload.";
    }
}
