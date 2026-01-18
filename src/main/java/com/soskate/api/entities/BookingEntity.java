package com.soskate.api.entities;

import com.soskate.api.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private InstructorEntity instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private SpotEntity spot;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 60;

    @Column(name = "max_participants", nullable = false)
    @Builder.Default
    private Integer maxParticipants = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingParticipantEntity> participants = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return status == BookingStatus.OPEN;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public boolean isCompleted() {
        return status == BookingStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    public int getConfirmedParticipantsCount() {
        return (int) participants.stream()
                .filter(BookingParticipantEntity::isConfirmed)
                .count();
    }

    public int getAvailablePlaces() {
        return maxParticipants - getConfirmedParticipantsCount();
    }

    public boolean hasAvailablePlaces() {
        return getAvailablePlaces() > 0;
    }

    /**
     * Calcule le prix total basé sur la durée et le prix horaire du service.
     */
    public int calculateTotalPriceCents() {
        if (service == null) return 0;
        double hours = durationMinutes / 60.0;
        return (int) Math.round(service.getBasePriceCents() * hours);
    }
}