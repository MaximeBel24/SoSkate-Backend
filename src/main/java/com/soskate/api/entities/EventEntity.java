package com.soskate.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_participants")
    private Integer maxParticipants;

//    @Column(name = "spot_id", nullable = false)
//    private Long spotId;
//
//    @Column(name = "registration_price_cents")
//    private Long registrationPriceCents;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @Column(updatable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Constructeur pour créer un nouvel événement (sans timestamps)
     */
    public EventEntity(String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                       Integer maxParticipants, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxParticipants = maxParticipants;
//        this.spotId = spotId;
//        this.registrationPriceCents = registrationPriceCents;
        this.isActive = isActive;
    }

    /**
     * Relation avec l'entité Spot (si elle existe)
     * Uncomment si vous avez une entité SpotEntity
     */
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", insertable = false, updatable = false)
    private SpotEntity spot;
    */

    /**
     * Validation métier : vérification que endTime est après startTime
     */
    @PrePersist
    @PreUpdate
    private void validateEventDates() {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalStateException("End time must be after start time");
        }
    }
}