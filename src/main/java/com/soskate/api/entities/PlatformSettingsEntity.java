package com.soskate.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_settings")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformSettingsEntity {

    @Id
    private Long id = 1L;

    @Column(name = "min_booking_hours_in_advance", nullable = false)
    @Builder.Default
    private Integer minBookingHoursInAdvance = 72;

    @Column(name = "auto_refund_hours_limit", nullable = false)
    @Builder.Default
    private Integer autoRefundHoursLimit = 48;

    @Column(name = "default_buffer_minutes", nullable = false)
    @Builder.Default
    private Integer defaultBufferMinutes = 30;

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
}