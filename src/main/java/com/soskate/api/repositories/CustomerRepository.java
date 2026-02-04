package com.soskate.api.repositories;

import com.soskate.api.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour la gestion des clients (riders) de SoSkate.
 * Contient les méthodes CRUD, de recherche, d'analytics et d'optimisation.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Recherche un client par email.
     * CRITIQUE pour l'authentification JWT et la validation d'inscription.
     *
     * @param email l'email du client
     * @return Optional contenant le client s'il existe
     */
    Optional<CustomerEntity> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà en base.
     * Utilisé pour éviter les doublons lors de l'inscription.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un email existe pour un autre customer (excluant l'ID donné).
     * Utilisé pour la validation d'unicité lors des mises à jour de profil.
     *
     * @param email l'email à vérifier
     * @param id l'ID du customer à exclure de la recherche
     * @return true si un autre customer possède cet email
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}