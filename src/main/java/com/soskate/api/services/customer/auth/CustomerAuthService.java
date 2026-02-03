package com.soskate.api.services.customer.auth;

import com.soskate.api.dto.auth.register.CustomerRegisterRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;

/**
 * Service dédié à l'authentification des customers.
 * Gère l'inscription et la vérification d'email.
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
    CustomerResponse registerCustomer(CustomerRegisterRequest customerRegisterDto);

    /**
     * Vérifie si un email existe déjà en base.
     * Utile pour la validation côté frontend avant soumission du formulaire.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean emailExists(String email);
}
