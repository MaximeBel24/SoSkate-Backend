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
 * Repository for instructor management.
 * Includes soft delete support (queries filter by deleted = false).
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface InstructorRepository extends JpaRepository<InstructorEntity, Long> {

    // ==================== Find by Unique Fields ====================

    /**
     * Finds a non-deleted instructor by email.
     *
     * @param email the instructor's email
     * @return Optional containing the instructor if it exists
     */
    Optional<InstructorEntity> findByEmailAndDeletedFalse(String email);

    /**
     * Finds an instructor by activation token.
     * Does not filter by deleted because it is used during the activation process.
     *
     * @param activationToken the activation token
     * @return Optional containing the instructor if it exists
     */
    Optional<InstructorEntity> findByActivationToken(String activationToken);

    // ==================== Existence Checks ====================

    /**
     * Checks if a non-deleted instructor exists with this email.
     *
     * @param email the email to check
     * @return true if the email exists
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * Checks if an email exists for another instructor (excluding the given ID).
     * Used for uniqueness validation during profile updates.
     *
     * @param email the email to check
     * @param id the instructor ID to exclude
     * @return true if another instructor has this email
     */
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);

    // ==================== Status-based Queries ====================

    /**
     * Finds all non-deleted instructors with a specific status.
     *
     * @param status the requested status
     * @return list of matching instructors
     */
    List<InstructorEntity> findByStatusAndDeletedFalse(InstructorStatus status);

    /**
     * Finds all active and non-deleted instructors.
     *
     * @return list of active instructors
     */
    default List<InstructorEntity> findAllActiveAndDeletedFalse() {
        return findByStatusAndDeletedFalse(InstructorStatus.ACTIVE);
    }

    /**
     * Finds all instructors with pending invitations.
     *
     * @return list of invited instructors
     */
    default List<InstructorEntity> findAllInvited() {
        return findByStatusAndDeletedFalse(InstructorStatus.INVITED);
    }

    // ==================== Specialty-based Queries ====================

    /**
     * Finds all non-deleted instructors with a given status and specialty.
     *
     * @param status the requested status
     * @param specialty the requested specialty
     * @return list of matching instructors
     */
    List<InstructorEntity> findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus status, SkateSpecialty specialty);

    /**
     * Finds all active instructors for a given specialty.
     *
     * @param specialty the requested specialty
     * @return list of active instructors with this specialty
     */
    default List<InstructorEntity> findActiveBySpecialty(SkateSpecialty specialty) {
        return findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus.ACTIVE, specialty);
    }

    // ==================== Token Expiration Queries ====================

    /**
     * Finds instructors with expired activation tokens.
     * Useful for cleanup jobs or notifications.
     *
     * @param status the requested status (usually INVITED)
     * @param now the current date/time
     * @return list of instructors with expired tokens
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = :status AND i.activationTokenExpiry < :now AND i.deleted = false")
    List<InstructorEntity> findByStatusAndActivationTokenExpired(
            @Param("status") InstructorStatus status,
            @Param("now") LocalDateTime now
    );

    /**
     * Finds all instructors with expired invitations.
     *
     * @return list of instructors with expired invitations
     */
    default List<InstructorEntity> findExpiredInvitations() {
        return findByStatusAndActivationTokenExpired(InstructorStatus.INVITED, LocalDateTime.now());
    }
}
