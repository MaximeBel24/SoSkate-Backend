package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global d'exceptions pour toute l'application
 * Gère les exceptions communes à tous les contrôleurs
 * Priorité basse pour laisser les handlers spécifiques traiter leurs exceptions en premier
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE) // Exécuté en dernier, après les handlers spécifiques
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les erreurs de validation des DTOs (@Valid)
     *
     * @param ex l'exception de validation
     * @param request la requête HTTP
     * @return ErrorResponse avec les détails des champs en erreur
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation errors - Path: {}, Field errors count: {}",
                request.getRequestURI(), ex.getBindingResult().getFieldErrorCount());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return ErrorResponse.validationError(request.getRequestURI(), fieldErrors);
    }

    /**
     * Gère les erreurs d'arguments invalides génériques
     *
     * @param ex l'exception d'argument invalide
     * @param request la requête HTTP
     * @return ErrorResponse avec le message d'erreur
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument - Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());

        return new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Gère les erreurs de conversion de type pour les paramètres de path et query
     * Par exemple : /services/abc au lieu de /services/123
     *
     * @param ex l'exception de type mismatch
     * @param request la requête HTTP
     * @return ErrorResponse avec les détails du type attendu
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch - Path: {}, Parameter: {}, Expected type: {}, Actual value: {}",
                request.getRequestURI(), ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue());

        Map<String, Object> details = Map.of(
                "parameter", ex.getName(),
                "rejectedValue", String.valueOf(ex.getValue()),
                "expectedType", ex.getRequiredType().getSimpleName()
        );

        return new ErrorResponse(
                "INVALID_PARAMETER_TYPE",
                String.format("Le paramètre '%s' doit être de type %s, mais '%s' a été fourni",
                        ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue()),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les erreurs de méthode HTTP non supportée
     * Par exemple : POST sur un endpoint qui n'accepte que GET
     *
     * @param ex l'exception de méthode non supportée
     * @param request la requête HTTP
     * @return ErrorResponse avec les méthodes supportées
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported - Path: {}, Method: {}, Supported methods: {}",
                request.getRequestURI(), ex.getMethod(), ex.getSupportedHttpMethods());

        Map<String, Object> details = Map.of(
                "requestedMethod", ex.getMethod(),
                "supportedMethods", ex.getSupportedHttpMethods()
        );

        return new ErrorResponse(
                "METHOD_NOT_ALLOWED",
                String.format("La méthode HTTP '%s' n'est pas supportée pour cette ressource. Méthodes supportées : %s",
                        ex.getMethod(), ex.getSupportedHttpMethods()),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les erreurs de type de contenu non supporté
     * Par exemple : envoi de XML sur un endpoint qui n'accepte que JSON
     *
     * @param ex l'exception de media type non supporté
     * @param request la requête HTTP
     * @return ErrorResponse avec les types supportés
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("Media type not supported - Path: {}, Content-Type: {}, Supported types: {}",
                request.getRequestURI(), ex.getContentType(), ex.getSupportedMediaTypes());

        Map<String, Object> details = Map.of(
                "requestedContentType", String.valueOf(ex.getContentType()),
                "supportedMediaTypes", ex.getSupportedMediaTypes()
        );

        return new ErrorResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                String.format("Le type de contenu '%s' n'est pas supporté. Types supportés : %s",
                        ex.getContentType(), ex.getSupportedMediaTypes()),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les erreurs de JSON malformé dans le body de la requête
     *
     * @param ex l'exception de message non lisible
     * @param request la requête HTTP
     * @return ErrorResponse avec le message d'erreur
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("HTTP message not readable - Path: {}, Message: {}", request.getRequestURI(), ex.getMessage());

        String userFriendlyMessage = "Le format des données envoyées est invalide";

        // Essayer de donner un message plus spécifique selon le type d'erreur
        if (ex.getMessage().contains("JSON")) {
            userFriendlyMessage = "Le format JSON envoyé est invalide ou malformé";
        } else if (ex.getMessage().contains("Required request body is missing")) {
            userFriendlyMessage = "Le corps de la requête est obligatoire mais manquant";
        }

        return new ErrorResponse(
                "MALFORMED_REQUEST_BODY",
                userFriendlyMessage,
                request.getRequestURI()
        );
    }

    /**
     * Gère les paramètres de requête manquants
     *
     * @param ex l'exception de paramètre manquant
     * @param request la requête HTTP
     * @return ErrorResponse avec le paramètre manquant
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing request parameter - Path: {}, Parameter: {}, Type: {}",
                request.getRequestURI(), ex.getParameterName(), ex.getParameterType());

        Map<String, Object> details = Map.of(
                "missingParameter", ex.getParameterName(),
                "expectedType", ex.getParameterType()
        );

        return new ErrorResponse(
                "MISSING_PARAMETER",
                String.format("Le paramètre obligatoire '%s' est manquant", ex.getParameterName()),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les variables de path manquantes
     *
     * @param ex l'exception de variable de path manquante
     * @param request la requête HTTP
     * @return ErrorResponse avec la variable manquante
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        log.error("Missing path variable - This should not happen in production - Path: {}, Variable: {}",
                request.getRequestURI(), ex.getVariableName());

        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Une erreur de configuration interne est survenue",
                request.getRequestURI()
        );
    }

    /**
     * Gère les erreurs 404 - Handler non trouvé
     *
     * @param ex l'exception de handler non trouvé
     * @param request la requête HTTP
     * @return ErrorResponse pour 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("No handler found - Path: {}, Method: {}", request.getRequestURI(), ex.getHttpMethod());

        Map<String, Object> details = Map.of(
                "requestedPath", request.getRequestURI(),
                "httpMethod", ex.getHttpMethod()
        );

        return new ErrorResponse(
                "ENDPOINT_NOT_FOUND",
                String.format("L'endpoint '%s %s' n'existe pas", ex.getHttpMethod(), request.getRequestURI()),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gestionnaire de fallback pour toutes les autres exceptions non gérées
     *
     * @param ex l'exception non gérée
     * @param request la requête HTTP
     * @return ErrorResponse générique pour erreur serveur
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception - Path: {}, Exception type: {}, Message: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, Object> details = Map.of(
                "exceptionType", ex.getClass().getSimpleName(),
                "errorId", java.util.UUID.randomUUID().toString() // ID unique pour traçabilité
        );

        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Une erreur interne inattendue est survenue. Veuillez contacter le support technique.",
                request.getRequestURI(),
                details
        );
    }
}