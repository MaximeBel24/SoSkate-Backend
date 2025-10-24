package com.soskate.api.exceptions.auth;

/**
 * Exception levée lorsque les identifiants de connexion sont invalides.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException() {
        super("Email ou mot de passe incorrect");
    }
}