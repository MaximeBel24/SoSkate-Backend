package com.soskate.api.entities;

import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "number_of_participants", nullable = false)
    @Builder.Default
    private Integer numberOfParticipants = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.PENDING_PAYMENT;

    @Column(name = "amount_cents")
    private Integer amountCents;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "invited_by", nullable = false)
    @Builder.Default
    private InvitedBy invitedBy = InvitedBy.SELF;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_customer_id")
    private CustomerEntity invitedByCustomer;

    @Column(name = "participants_notes", length = 500)
    private String participantsNotes;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

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

    // Méthodes utilitaires
    public boolean isConfirmed() {
        return status == ParticipantStatus.CONFIRMED;
    }

    public boolean canBeCancelled() {
        return status == ParticipantStatus.INVITED
                || status == ParticipantStatus.PENDING_PAYMENT
                || status == ParticipantStatus.CONFIRMED;
    }
}