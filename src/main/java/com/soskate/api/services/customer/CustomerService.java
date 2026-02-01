package com.soskate.api.services.customer;

import com.soskate.api.dto.customer.CustomerUpdateRequest;
import com.soskate.api.dto.customer.CustomerResponse;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.exceptions.auth.EmailAlreadyExistsException;

/**
 * Service pour la gestion du profil Customer.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface CustomerService {

    /**
     * Met à jour le profil d'un customer.
     * Seuls les champs non-null sont mis à jour (mise à jour partielle).
     *
     * @param customerId l'ID du customer à mettre à jour
     * @param request les données à mettre à jour
     * @return le customer mis à jour
     * @throws CustomerNotFoundException si le customer n'existe pas
     * @throws EmailAlreadyExistsException si le nouvel email est déjà utilisé
     */
    CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request);

    /**
     * Récupère le profil d'un customer par son ID.
     *
     * @param customerId l'ID du customer
     * @return le profil du customer
     * @throws CustomerNotFoundException si le customer n'existe pas
     */
    CustomerResponse getCustomerById(Long customerId);
}