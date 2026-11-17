package com.soskate.api.repositories;

import com.soskate.api.entities.AvailabilityBlockEntity;
import com.soskate.api.services.availability.AgendaBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlockEntity, Long> {

    /**
     * An instructor's blocks over a period, start included and end excluded, in chronological order.
     */
    @Query("""
        SELECT new com.soskate.api.services.availability.AgendaBlock(b.startAt, b.booking.id)
        FROM AvailabilityBlockEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startAt >= :from
        AND b.startAt < :to
        ORDER BY b.startAt
    """)
    List<AgendaBlock> findAgendaBlocks(
            @Param("instructorId") Long instructorId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Modifying
    @Query("DELETE FROM AvailabilityBlockEntity b WHERE b.instructor.id = :instructorId AND b.startAt IN :startTimes")
    int deleteBlocks(
            @Param("instructorId") Long instructorId,
            @Param("startTimes") Collection<LocalDateTime> startTimes
    );

    /**
     * Frees every block a session occupies.
     */
    @Modifying
    @Query("UPDATE AvailabilityBlockEntity b SET b.booking = NULL WHERE b.booking.id = :bookingId")
    int releaseBooking(@Param("bookingId") Long bookingId);
}
