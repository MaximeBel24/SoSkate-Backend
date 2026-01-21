package com.soskate.api.repositories;

import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.InstructorSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructorSpotRepository extends JpaRepository<InstructorSpotEntity, Long> {

    List<InstructorSpotEntity> findByInstructorId(Long instructorId);

    List<InstructorSpotEntity> findBySpotId(Long spotId);

    boolean existsByInstructorIdAndSpotId(Long instructorId, Long spotId);

    void deleteByInstructorIdAndSpotId(Long instructorId, Long spotId);

    @Query("SELECT is.spot.id FROM InstructorSpotEntity is WHERE is.instructor.id = :instructorId")
    List<Long> findSpotIdsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT is.instructor FROM InstructorSpotEntity is WHERE is.spot.id = :spotId AND is.instructor.status = 'ACTIVE'")
    List<InstructorEntity> findActiveInstructorsBySpotId(@Param("spotId") Long spotId);
}