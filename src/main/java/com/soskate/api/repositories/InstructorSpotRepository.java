package com.soskate.api.repositories;

import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.InstructorSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des associations instructeur-spot.
 * Permet de savoir quels instructeurs enseignent sur quels spots.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface InstructorSpotRepository extends JpaRepository<InstructorSpotEntity, Long> {

    /**
     * Trouve toutes les associations pour un instructeur donné.
     *
     * @param instructorId l'ID de l'instructeur
     * @return liste des associations instructeur-spot
     */
    List<InstructorSpotEntity> findByInstructorId(Long instructorId);

    /**
     * Vérifie si une association existe entre un instructeur et un spot.
     *
     * @param instructorId l'ID de l'instructeur
     * @param spotId l'ID du spot
     * @return true si l'association existe
     */
    boolean existsByInstructorIdAndSpotId(Long instructorId, Long spotId);

    /**
     * Supprime l'association entre un instructeur et un spot.
     *
     * @param instructorId l'ID de l'instructeur
     * @param spotId l'ID du spot
     */
    void deleteByInstructorIdAndSpotId(Long instructorId, Long spotId);

    /**
     * Trouve tous les instructeurs actifs et non supprimés qui enseignent sur un spot.
     *
     * @param spotId l'ID du spot
     * @return liste des instructeurs actifs sur ce spot
     */
    @Query("""
        SELECT is.instructor FROM InstructorSpotEntity is
        WHERE is.spot.id = :spotId
        AND is.instructor.status = 'ACTIVE'
        AND is.instructor.deleted = false
    """)
    List<InstructorEntity> findActiveInstructorsBySpotId(@Param("spotId") Long spotId);
}
