package com.soskate.api.exceptions.auth;

/**
 * Exception levée lorsqu'un utilisateur tente de s'inscrire avec un email déjà existant.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Constructeur avec message personnalisé.
     *
     * @param message le message d'erreur
     */
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructeur avec email en paramètre.
     *
     * @param email l'email qui existe déjà
     */
    public EmailAlreadyExistsException(String email, boolean isEmail) {
        super(String.format("Un compte avec l'email '%s' existe déjà", email));
    }
}
