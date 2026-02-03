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
     * Find a no deleted instructor by their email address.
     */
    Optional<InstructorEntity> findByEmailAndDeletedFalse(String email);

    /**
     * Find an instructor by their activation token.
     */
    Optional<InstructorEntity> findByActivationToken(String activationToken);

    // ==================== Existence Checks ====================

    /**
     * Check if an instructor with the given email already exists.
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * Checks if an email exists for another instructor (excluding the given ID).
     * Used for email uniqueness validation during profile updates.
     *
     * @param email the email to check
     * @param id the instructor ID to exclude from the search
     * @return true if another instructor has this email
     */
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);


    // ==================== Status-based Queries ====================

    /**
     * Find all instructors with a specific status.
     */
    List<InstructorEntity> findByStatusAndDeletedFalse(InstructorStatus status);

    /**
     * Find all active and not deleted instructors.
     */
    default List<InstructorEntity> findAllActiveAndDeletedFalse() {
        return findByStatusAndDeletedFalse(InstructorStatus.ACTIVE);
    }

    /**
     * Find all instructors with pending invitations.
     */
    default List<InstructorEntity> findAllInvited() {
        return findByStatusAndDeletedFalse(InstructorStatus.INVITED);
    }

    // ==================== Specialty-based Queries ====================

    /**
     * Find all active instructors with a specific specialty.
     */
    List<InstructorEntity> findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus status, SkateSpecialty specialty);

    /**
     * Find all active instructors for a specific specialty.
     */
    default List<InstructorEntity> findActiveBySpecialty(SkateSpecialty specialty) {
        return findByStatusAndSpecialtyAndDeletedFalse(InstructorStatus.ACTIVE, specialty);
    }

    /**
     * Find instructors with expired activation tokens.
     * Useful for cleanup jobs or notifications.
     */
    @Query("SELECT i FROM InstructorEntity i WHERE i.status = :status AND i.activationTokenExpiry < :now AND i.deleted = false")
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