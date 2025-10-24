package com.soskate.api.services.customer.auth;

import com.soskate.api.dtos.auth.login.LoginRequestDTO;
import com.soskate.api.dtos.auth.login.LoginResponseDTO;
import com.soskate.api.dtos.auth.register.CustomerRegisterRequestDTO;
import com.soskate.api.dtos.customer.CustomerResponseDTO;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;
import com.soskate.api.exceptions.auth.BadCredentialsException;

/**
 * Service dédié à l'authentification des customers.
 * Gère l'inscription, la connexion, la vérification d'email et le reset de password.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface CustomerAuthService {

    /**
     * Inscrit un nouveau customer dans le système.
     * Vérifie l'unicité de l'email, hash le mot de passe et sauvegarde en base.
     *
     * @param customerRegisterDto les données d'inscription
     * @return le customer créé (sans le password)
     * @throws EmailAlreadyExistsException si l'email existe déjà
     */
    CustomerResponseDTO registerCustomer(CustomerRegisterRequestDTO customerRegisterDto);

    /**
     * Authentifie un customer avec son email et mot de passe.
     *
     * @param loginRequest email + password
     * @return les informations de connexion (customer + token JWT si applicable)
     * @throws BadCredentialsException si email ou password invalide
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * Vérifie si un email existe déjà en base.
     * Utile pour la validation côté frontend avant soumission du formulaire.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean emailExists(String email);

//    /**
//     * Envoie un email de vérification après l'inscription.
//     * (Optionnel pour le MVP, mais utile pour éviter les faux comptes)
//     *
//     * @param email l'email du customer
//     */
//    void sendVerificationEmail(String email);
//
//    /**
//     * Vérifie le token de confirmation d'email.
//     *
//     * @param token le token reçu par email
//     * @return true si la vérification a réussi
//     */
//    boolean verifyEmail(String token);
//
//    /**
//     * Initie une demande de réinitialisation de mot de passe.
//     * Envoie un email avec un lien de reset.
//     *
//     * @param email l'email du customer
//     */
//    void requestPasswordReset(String email);
//
//    /**
//     * Réinitialise le mot de passe avec le token reçu par email.
//     *
//     * @param token le token de reset
//     * @param newPassword le nouveau mot de passe
//     * @return true si la réinitialisation a réussi
//     */
//    boolean resetPassword(String token, String newPassword);
//
//    /**
//     * Change le mot de passe d'un customer connecté.
//     * Vérifie que l'ancien mot de passe est correct avant de le changer.
//     *
//     * @param customerId l'ID du customer
//     * @param oldPassword l'ancien mot de passe
//     * @param newPassword le nouveau mot de passe
//     * @return true si le changement a réussi
//     * @throws BadCredentialsException si l'ancien password est incorrect
//     */
//    boolean changePassword(Long customerId, String oldPassword, String newPassword);
}