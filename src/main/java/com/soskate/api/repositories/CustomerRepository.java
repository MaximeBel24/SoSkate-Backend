package com.soskate.api.repositories;

import com.soskate.api.entities.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing SoSkate customers (riders).
 * Contains CRUD, search, analytics, and optimization methods.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Finds a customer by email.
     * CRITICAL for JWT authentication and registration validation.
     *
     * @param email the customer's email
     * @return Optional containing the customer if it exists
     */
    Optional<CustomerEntity> findByEmail(String email);

    /**
     * Checks if an email already exists in the database.
     * Used to prevent duplicates during registration.
     *
     * @param email the email to check
     * @return true if the email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if an email exists for another customer (excluding the given ID).
     * Used for uniqueness validation during profile updates.
     *
     * @param email the email to check
     * @param id the customer ID to exclude from the search
     * @return true if another customer has this email
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Counts customers registered within a given time range.
     * Used for dashboard KPIs (new customers this month).
     *
     * @param start start of the period (inclusive)
     * @param end end of the period (exclusive)
     * @return number of customers registered in the period
     */
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves the 10 most recently registered customers.
     * Used for the admin dashboard's recent registrations feed.
     *
     * @return list of the 10 latest customers, sorted by creation date descending
     */
    List<CustomerEntity> findTop10ByOrderByCreatedAtDesc();

}