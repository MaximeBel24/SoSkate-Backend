package com.soskate.api.exceptions.service;

import org.springframework.dao.DataAccessException;

/**
 * Exception lancée lors d'erreurs d'accès aux données des services
 * Encapsule les erreurs de base de données ou de persistence
 */
public class ServiceDataRetrievalException extends RuntimeException {

    /**
     * Constructeur avec message personnalisé
     *
     * @param message le message d'erreur spécifique
     */
    public ServiceDataRetrievalException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     *
     * @param message le message d'erreur spécifique
     * @param cause la cause originale de l'erreur
     */
    public ServiceDataRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur pour encapsuler une DataAccessException
     * Utilisé pour les erreurs de base de données Spring
     *
     * @param cause l'exception d'accès aux données originale
     */
    public ServiceDataRetrievalException(DataAccessException cause) {
        super("Database access error occurred while processing service data: " + cause.getMessage(), cause);
    }

    /**
     * Constructeur pour encapsuler toute exception technique
     * Utilisé comme fallback pour les erreurs inattendues
     *
     * @param cause l'exception technique originale
     */
    public ServiceDataRetrievalException(Exception cause) {
        super("Technical error occurred while retrieving service data: " + cause.getMessage(), cause);
    }

    /**
     * Factory method pour créer une exception lors d'une opération spécifique
     *
     * @param operation l'opération qui a échoué (ex: "creating", "updating", "deleting")
     * @param cause la cause originale
     * @return une nouvelle instance avec un message contextualisé
     */
    public static ServiceDataRetrievalException forOperation(String operation, Throwable cause) {
        return new ServiceDataRetrievalException(
                String.format("Failed to %s service data: %s", operation, cause.getMessage()),
                cause
        );
    }

    /**
     * Factory method pour créer une exception lors d'un accès par ID
     *
     * @param serviceId l'ID du service qui a causé l'erreur
     * @param cause la cause originale
     * @return une nouvelle instance avec un message contextualisé
     */
    public static ServiceDataRetrievalException forServiceId(Long serviceId, Throwable cause) {
        return new ServiceDataRetrievalException(
                String.format("Failed to access data for service ID %d: %s", serviceId, cause.getMessage()),
                cause
        );
    }
}