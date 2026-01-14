package com.soskate.api.repositories;

import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {

    List<AvailabilityEntity> findByInstructorIdAndDate(Long instructorId, LocalDate date);

    List<AvailabilityEntity> findByInstructorIdAndDateBetween(
            Long instructorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AvailabilityEntity> findByInstructorIdAndStatus(
            Long instructorId,
            AvailabilityStatus status
    );

    @Query("""
        SELECT a FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date = :date
        AND a.status = 'AVAILABLE'
        ORDER BY a.startTime
    """)
    List<AvailabilityEntity> findAvailableByInstructorAndDate(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date
    );

    @Query("""
        SELECT a FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date >= :startDate
        AND a.date <= :endDate
        AND a.status = 'AVAILABLE'
        ORDER BY a.date, a.startTime
    """)
    List<AvailabilityEntity> findAvailableByInstructorAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date = :date
        AND a.startTime < :endTime
        AND a.endTime > :startTime
        AND a.status = 'AVAILABLE'
    """)
    boolean existsOverlapping(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );
}