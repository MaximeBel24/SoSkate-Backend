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
}