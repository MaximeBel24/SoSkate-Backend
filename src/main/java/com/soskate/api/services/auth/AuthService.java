package com.soskate.api.services.auth;

import com.soskate.api.dto.auth.login.LoginRequest;
import com.soskate.api.dto.auth.login.UnifiedLoginResponse;
import com.soskate.api.exceptions.auth.BadCredentialsException;

/**
 * Service d'authentification unifié.
 * Gère la connexion pour tous les types d'utilisateurs (Customer et Instructor).
 *
 * Ce service détecte automatiquement le type d'utilisateur en cherchant
 * d'abord dans les Customers, puis dans les Instructors.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur (Customer ou Instructor) avec son email et mot de passe.
     *
     * La méthode cherche d'abord dans les Customers, puis dans les Instructors.
     * Le rôle retourné permet au frontend d'adapter l'interface.
     *
     * @param loginRequest email + password
     * @return les informations de connexion unifiées avec le rôle
     * @throws BadCredentialsException si email ou password invalide
     */
    UnifiedLoginResponse login(LoginRequest loginRequest);

    /**
     * Vérifie si un email existe déjà dans le système (Customer OU Instructor).
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean emailExists(String email);
}