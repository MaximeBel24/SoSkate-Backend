package com.soskate.api.repositories;

import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.SkateSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des instructeurs.
 * Inclut le support du soft delete (les requêtes filtrent par deleted = false).
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {

    // ==================== Find by Unique Fields ====================

    /**
     * Trouve un instructeur non supprimé par son email.
     *
     * @param email l'email de l'instructeur
     * @return Optional contenant l'instructeur s'il existe
     */
    Optional<InstructorEntity> findByEmailAndDeletedFalse(String email);

    /**
     * Trouve un instructeur par son token d'activation.
     * Ne filtre pas par deleted car utilisé pendant le processus d'activation.
     *
     * @param activationToken le token d'activation
     * @return Optional contenant l'instructeur s'il existe
     */
    Optional<InstructorEntity> findByActivationToken(String activationToken);

    // ==================== Existence Checks ====================

    /**
     * Vérifie si un instructeur non supprimé existe avec cet email.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * Vérifie si un email existe pour un autre instructeur (excluant l'ID donné).
     * Utilisé pour la validation d'unicité lors des mises à jour de profil.
     *
     * @param email l'email à vérifier
     * @param id l'ID de l'instructeur à exclure
     * @return true si un autre instructeur possède cet email
     */
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);

    // ==================== Status-based Queries ====================

    /**
     * Trouve tous les instructeurs non supprimés avec un statut spécifique.
     *
     * @param status le statut recherché
     * @return liste des instructeurs correspondants
     */
    List<InstructorEntity> findByStatusAndDeletedFalse(InstructorStatus status);

    /**
     * Trouve tous les instructeurs actifs et non supprimés.
     *
     * @return liste des instructeurs actifs
     */
    default List<InstructorEntity> findAllActiveAndDeletedFalse() {
        return findByStatusAndDeletedFalse(InstructorStatus.ACTIVE);
    }

    /**
     * Trouve tous les instructeurs avec des invitations en attente.
     *
     * @return liste des instructeurs invités
     */
    default List<InstructorEntity> findAllInvited() {
        return findByStatusAndDeletedFalse(InstructorStatus.INVITED);
    }

    // ==================== Specialty-based Queries ====================

    /**
     * Trouve tous les instructeurs non supprimés avec un statut et une spécialité donnés.
     *
     * @param status le statut recherché
     * @param specialty la spécialité recherchée
     * @return liste des instructeurs correspondants
     */
    List<InstructorEntity> findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus status, SkateSpecialty specialty);

    /**
     * Trouve tous les instructeurs actifs pour une spécialité donnée.
     *
     * @param specialty la spécialité recherchée
     * @return liste des instructeurs actifs avec cette spécialité
     */
    default List<InstructorEntity> findActiveBySpecialty(SkateSpecialty specialty) {
        return findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus.ACTIVE, specialty);
    }

    // ==================== Token Expiration Queries ====================

    /**
     * Trouve les instructeurs avec des tokens d'activation expirés.
     * Utile pour les jobs de nettoyage ou les notifications.
     *
     * @param status le statut recherché (généralement INVITED)
     * @param now la date/heure actuelle
     * @return liste des instructeurs avec tokens expirés
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = :status AND i.activationTokenExpiry < :now AND i.deleted = false")
    List<InstructorEntity> findByStatusAndActivationTokenExpired(
            @Param("status") InstructorStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * Trouve tous les instructeurs avec des invitations expirées.
     *
     * @return liste des instructeurs avec invitations expirées
     */
    default List<InstructorEntity> findExpiredInvitations() {
        return findByStatusAndActivationTokenExpired(InstructorStatus.INVITED, LocalDateTime.now());
    }
}
