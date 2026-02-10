package com.soskate.api.repositories;

import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.InstructorSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing instructor-spot associations.
 * Allows querying which instructors teach at which spots.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface InstructorSpotRepository extends JpaRepository<InstructorSpotEntity, Long> {

    /**
     * Finds all associations for a given instructor.
     *
     * @param instructorId the instructor ID
     * @return list of instructor-spot associations
     */
    List<InstructorSpotEntity> findByInstructorId(Long instructorId);

    /**
     * Checks whether an association exists between an instructor and a spot.
     *
     * @param instructorId the instructor ID
     * @param spotId the spot ID
     * @return true if the association exists
     */
    boolean existsByInstructorIdAndSpotId(Long instructorId, Long spotId);

    /**
     * Deletes the association between an instructor and a spot.
     *
     * @param instructorId the instructor ID
     * @param spotId the spot ID
     */
    void deleteByInstructorIdAndSpotId(Long instructorId, Long spotId);

    /**
     * Finds all active and non-deleted instructors who teach at a spot.
     *
     * @param spotId the spot ID
     * @return list of active instructors at this spot
     */
    @Query("""
        SELECT is.instructor FROM InstructorSpotEntity is
        WHERE is.spot.id = :spotId
        AND is.instructor.status = 'ACTIVE'
        AND is.instructor.deleted = false
    """)
    List<InstructorEntity> findActiveInstructorsBySpotId(@Param("spotId") Long spotId);
}
