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

@Repository
public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {

    // ==================== Find by Unique Fields ====================

    /**
     * Find an instructor by their email address.
     */
    Optional<InstructorEntity> findByEmail(String email);

    /**
     * Find an instructor by their activation token.
     */
    Optional<InstructorEntity> findByActivationToken(String activationToken);

    // ==================== Existence Checks ====================

    /**
     * Check if an instructor with the given email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Check if an activation token exists.
     */
    boolean existsByActivationToken(String activationToken);

    // ==================== Status-based Queries ====================

    /**
     * Find all instructors with a specific status.
     */
    List<InstructorEntity> findByStatus(InstructorStatus status);

    /**
     * Find all active instructors.
     */
    default List<InstructorEntity> findAllActive() {
        return findByStatus(InstructorStatus.ACTIVE);
    }

    /**
     * Find all instructors with pending invitations.
     */
    default List<InstructorEntity> findAllInvited() {
        return findByStatus(InstructorStatus.INVITED);
    }

    // ==================== Specialty-based Queries ====================

    /**
     * Find all active instructors with a specific specialty.
     */
    List<InstructorEntity> findByStatusAndSpecialty(InstructorStatus status, SkateSpecialty specialty);

    /**
     * Find all active instructors for a specific specialty.
     */
    default List<InstructorEntity> findActiveBySpecialty(SkateSpecialty specialty) {
        return findByStatusAndSpecialty(InstructorStatus.ACTIVE, specialty);
    }

    // ==================== Token Expiration Queries ====================

    /**
     * Find instructors with expired activation tokens.
     * Useful for cleanup jobs or notifications.
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = :status AND i.activationTokenExpiry < :now")
    List<InstructorEntity> findByStatusAndActivationTokenExpired(
            @Param("status") InstructorStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * Find all instructors with expired invitations.
     */
    default List<InstructorEntity> findExpiredInvitations() {
        return findByStatusAndActivationTokenExpired(InstructorStatus.INVITED, LocalDateTime.now());
    }

    /**
     * Find instructors invited but not yet activated, with valid tokens.
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = 'INVITED' AND i.activationTokenExpiry > :now")
    List<InstructorEntity> findPendingInvitationsWithValidToken(@Param("now") LocalDateTime now);

    // ==================== Search Queries ====================

    /**
     * Search instructors by name (firstname or lastname).
     */
    @Query("SELECT i FROM InstructorEntity i WHERE " +
            "LOWER(i.firstname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.lastname) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<InstructorEntity> searchByName(@Param("query") String query);

    /**
     * Search active instructors by name.
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = 'ACTIVE' AND (" +
            "LOWER(i.firstname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(i.lastname) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<InstructorEntity> searchActiveByName(@Param("query") String query);

    // ==================== Statistics Queries ====================

    /**
     * Count instructors by status.
     */
    long countByStatus(InstructorStatus status);

    /**
     * Count active instructors by specialty.
     */
    long countByStatusAndSpecialty(InstructorStatus status, SkateSpecialty specialty);
}