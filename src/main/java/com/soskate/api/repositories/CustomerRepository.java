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

//    // ========================================
//    // 🥈 NIVEAU 2 : MÉTIER ET RECHERCHE
//    // ========================================
//
//    /**
//     * Recherche des clients par nom de famille (insensible à la casse).
//     * Utile pour la barre de recherche du backoffice admin.
//     *
//     * @param lastname le nom de famille (partiel ou complet)
//     * @return liste des clients correspondants
//     */
//    List<CustomerEntity> findByLastnameContainingIgnoreCase(String lastname);
//
//    /**
//     * Recherche avancée par prénom OU nom de famille (insensible à la casse).
//     * Permet une recherche plus flexible dans le backoffice.
//     *
//     * @param firstname le prénom à rechercher
//     * @param lastname le nom à rechercher
//     * @return liste des clients correspondants
//     */
//    List<CustomerEntity> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
//            String firstname, String lastname);
//
//    /**
//     * Récupère tous les clients avec pagination.
//     * Essentiel pour l'affichage du backoffice avec de nombreux clients.
//     *
//     * @param pageable paramètres de pagination (page, size, sort)
//     * @return page de clients
//     */
//    Page<CustomerEntity> findAll(Pageable pageable);
//
//    /**
//     * Recherche un client par numéro de téléphone.
//     * Utile pour le support client ou la recherche admin.
//     *
//     * @param phone le numéro de téléphone
//     * @return Optional contenant le client s'il existe
//     */
//    Optional<CustomerEntity> findByPhone(String phone);
//
//    // ========================================
//    // 🥉 NIVEAU 3 : STATISTIQUES ET ANALYTICS
//    // ========================================
//
//    /**
//     * Compte le nombre de clients inscrits entre deux dates.
//     * Utilisé pour le dashboard admin et les statistiques d'inscription.
//     *
//     * @param start date de début
//     * @param end date de fin
//     * @return nombre de clients inscrits dans la période
//     */
//    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
//
//    /**
//     * Récupère les clients inscrits après une date donnée.
//     * Utile pour lister les nouveaux inscrits récents.
//     *
//     * @param date la date de référence
//     * @return liste des clients inscrits après cette date
//     */
//    List<CustomerEntity> findByCreatedAtAfter(LocalDateTime date);
//
//    /**
//     * Recherche les clients actifs (ayant au moins X réservations).
//     * Permet de segmenter les clients engagés.
//     *
//     * @param minBookings nombre minimum de réservations
//     * @return liste des clients actifs
//     */
//    @Query("SELECT c FROM CustomerEntity c WHERE SIZE(c.bookings) >= :minBookings")
//    List<CustomerEntity> findActiveCustomers(@Param("minBookings") int minBookings);
//
//    /**
//     * Recherche les clients inactifs (inscrits mais sans réservation).
//     * Utile pour les campagnes de relance marketing.
//     *
//     * @return liste des clients sans aucune réservation
//     */
//    @Query("SELECT c FROM CustomerEntity c WHERE SIZE(c.bookings) = 0")
//    List<CustomerEntity> findCustomersWithoutBookings();
//
//    // ========================================
//    // 🏅 NIVEAU 4 : OPTIMISATIONS PERFORMANCE
//    // ========================================
//
//    /**
//     * Récupère un client avec ses réservations et commentaires en une seule requête.
//     * Utilise EntityGraph pour éviter les problèmes N+1 queries.
//     *
//     * @param id l'identifiant du client
//     * @return Optional contenant le client avec ses relations chargées
//     */
//    @EntityGraph(attributePaths = {"bookings", "comments"})
//    Optional<CustomerEntity> findWithBookingsAndCommentsById(Long id);
//
//    /**
//     * Récupère tous les clients ayant réservé avec un instructeur spécifique.
//     * Permet à un instructeur de voir sa "clientèle".
//     *
//     * @param instructorId l'identifiant de l'instructeur
//     * @return liste des clients distincts ayant réservé avec cet instructeur
//     */
//    @Query("SELECT DISTINCT c FROM CustomerEntity c JOIN c.bookings b WHERE b.instructor.id = :instructorId")
//    List<CustomerEntity> findByInstructorId(@Param("instructorId") Long instructorId);
//
//    /**
//     * Supprime un client par son email.
//     * Utile pour le droit à l'oubli (RGPD).
//     *
//     * @param email l'email du client à supprimer
//     */
//    void deleteByEmail(String email);
//
//    // ========================================
//    // 📊 BONUS : REQUÊTES STATISTIQUES AVANCÉES
//    // ========================================
//
//    /**
//     * Compte le nombre total de réservations effectuées par un client.
//     *
//     * @param customerId l'identifiant du client
//     * @return nombre de réservations
//     */
//    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.customer.id = :customerId")
//    Long countBookingsByCustomerId(@Param("customerId") Long customerId);
//
//    /**
//     * Récupère les clients triés par nombre de réservations décroissant.
//     * Permet d'identifier les "meilleurs clients".
//     *
//     * @param pageable paramètres de pagination
//     * @return page des clients les plus actifs
//     */
//    @Query("SELECT c FROM CustomerEntity c ORDER BY SIZE(c.bookings) DESC")
//    Page<CustomerEntity> findMostActiveCustomers(Pageable pageable);
}