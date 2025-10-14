package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.common.ErrorResponse;
import com.soskate.api.exceptions.service.ServiceAlreadyExistsException;
import com.soskate.api.exceptions.service.ServiceDataRetrievalException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Gestionnaire d'exceptions spécifique aux services
 * Gère toutes les exceptions métier liées aux services
 */
@ControllerAdvice
@Order(1) // Priorité haute pour traiter les exceptions spécifiques avant le handler global
public class ServiceExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    /**
     * Gère les cas où un service demandé n'existe pas
     *
     * @param ex l'exception ServiceNotFoundException
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 404
     */
    @ExceptionHandler(ServiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleServiceNotFound(ServiceNotFoundException ex, HttpServletRequest request) {
        log.warn("Service not found - Path: {}, Service ID: {}, Message: {}",
                request.getRequestURI(), ex.getServiceId(), ex.getMessage());

        Map<String, Object> details = Map.of(
                "serviceId", ex.getServiceId(),
                "requestedPath", request.getRequestURI()
        );

        return new ErrorResponse(
                "SERVICE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les cas où un service existe déjà (conflit lors de création/mise à jour)
     *
     * @param ex l'exception ServiceAlreadyExistsException
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 409
     */
    @ExceptionHandler(ServiceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleServiceAlreadyExists(ServiceAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Service already exists - Path: {}, Service name: '{}', Service type: '{}', Message: {}",
                request.getRequestURI(), ex.getServiceName(), ex.getServiceType(), ex.getMessage());

        Map<String, Object> details = Map.of(
                "serviceName", ex.getServiceName(),
                "serviceType", ex.getServiceType(),
                "conflictReason", "Un service avec ce nom et ce type existe déjà"
        );

        return new ErrorResponse(
                "SERVICE_ALREADY_EXISTS",
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les erreurs de récupération de données (problèmes de base de données)
     *
     * @param ex l'exception ServiceDataRetrievalException
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 500
     */
    @ExceptionHandler(ServiceDataRetrievalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServiceDataRetrievalException(ServiceDataRetrievalException ex, HttpServletRequest request) {
        log.error("Service data retrieval error - Path: {}, Message: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        // Ne pas exposer les détails techniques aux clients
        return new ErrorResponse(
                "SERVICE_DATA_ERROR",
                "Une erreur technique est survenue lors de l'accès aux données du service. Veuillez réessayer plus tard.",
                request.getRequestURI()
        );
    }

    /**
     * Gère les erreurs génériques d'accès aux données (DataAccessException)
     * Fallback pour les erreurs de base de données non capturées spécifiquement
     *
     * @param ex l'exception DataAccessException
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 500
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        log.error("Database access error - Path: {}, Exception type: {}, Message: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, Object> details = Map.of(
                "errorType", "DATABASE_ERROR",
                "component", "SERVICE"
        );

        return new ErrorResponse(
                "DATABASE_ACCESS_ERROR",
                "Une erreur de base de données est survenue. Veuillez contacter le support technique.",
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les arguments invalides spécifiques aux services
     * Par exemple : ID null, valeurs négatives, etc.
     *
     * @param ex l'exception IllegalArgumentException
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        // Vérifier si l'exception provient bien du contexte Service
        String path = request.getRequestURI();
        if (path.contains("/services")) {
            log.warn("Invalid argument for service operation - Path: {}, Message: {}", path, ex.getMessage());

            return new ErrorResponse(
                    "INVALID_SERVICE_ARGUMENT",
                    ex.getMessage(),
                    path
            );
        }

        // Sinon, laisser le GlobalExceptionHandler s'en occuper
        throw ex;
    }

    /**
     * Gère les erreurs de concurrence (OptimisticLockingFailureException)
     * Utile si vous implémentez la gestion de la concurrence optimiste
     *
     * @param ex l'exception de verrouillage optimiste
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 409
     */
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLockingFailure(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {

        log.warn("Optimistic locking failure for service - Path: {}, Entity: {}",
                request.getRequestURI(), ex.getPersistentClassName());

        Map<String, Object> details = Map.of(
                "errorType", "CONCURRENT_MODIFICATION",
                "entityType", "SERVICE"
        );

        return new ErrorResponse(
                "SERVICE_CONCURRENT_MODIFICATION",
                "Le service a été modifié par un autre utilisateur. Veuillez actualiser et réessayer.",
                request.getRequestURI(),
                details
        );
    }

    /**
     * Gère les erreurs de timeout spécifiques aux opérations de service
     *
     * @param ex l'exception de timeout
     * @param request la requête HTTP pour récupérer le chemin
     * @return ErrorResponse avec code 408
     */
    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponse handleTimeoutException(java.util.concurrent.TimeoutException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.contains("/services")) {
            log.error("Service operation timeout - Path: {}, Message: {}", path, ex.getMessage());

            return new ErrorResponse(
                    "SERVICE_OPERATION_TIMEOUT",
                    "L'opération sur le service a pris trop de temps. Veuillez réessayer.",
                    path
            );
        }

        // Laisser le handler global s'en occuper si ce n'est pas lié aux services
        throw new RuntimeException(ex);
    }
}