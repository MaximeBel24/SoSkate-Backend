package com.soskate.api.repositories;

import com.soskate.api.entities.BookingParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des participations aux réservations.
 * Gère la relation entre les customers et leurs bookings.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipantEntity, Long> {

    /**
     * Trouve toutes les participations pour un booking donné.
     *
     * @param bookingId l'ID du booking
     * @return liste des participations
     */
    List<BookingParticipantEntity> findByBookingId(Long bookingId);

    /**
     * Compte le nombre total de participants confirmés pour un booking.
     * Utilise COALESCE pour retourner 0 si aucun participant.
     *
     * @param bookingId l'ID du booking
     * @return nombre total de participants confirmés
     */
    @Query("""
        SELECT COALESCE(SUM(bp.numberOfParticipants), 0)
        FROM BookingParticipantEntity bp
        WHERE bp.booking.id = :bookingId
        AND bp.status = 'CONFIRMED'
    """)
    int countConfirmedParticipants(@Param("bookingId") Long bookingId);

    /**
     * Récupère toutes les participations d'un customer avec les détails complets.
     * Utilise JOIN FETCH pour éviter les problèmes N+1.
     *
     * @param customerId l'ID du customer
     * @return liste des participations avec booking, instructor, spot et service
     */
    @Query("SELECT bp FROM BookingParticipantEntity bp " +
            "JOIN FETCH bp.booking b " +
            "JOIN FETCH b.instructor " +
            "JOIN FETCH b.spot " +
            "JOIN FETCH b.service " +
            "WHERE bp.customer.id = :customerId " +
            "ORDER BY b.startTime DESC")
    List<BookingParticipantEntity> findAllByCustomerIdWithDetails(@Param("customerId") Long customerId);

    /**
     * Trouve une participation par son ID et l'ID du customer.
     * Utilisé pour vérifier qu'un customer accède bien à sa propre participation.
     *
     * @param id l'ID de la participation
     * @param customerId l'ID du customer
     * @return Optional contenant la participation si elle existe et appartient au customer
     */
    Optional<BookingParticipantEntity> findByIdAndCustomerId(Long id, Long customerId);

}
