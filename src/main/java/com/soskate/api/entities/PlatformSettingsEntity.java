package com.soskate.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Column(name = "auto_refund_hours_limit", nullable = false)
    @Builder.Default
    private Integer autoRefundHoursLimit = 48;

    @Column(name = "default_buffer_minutes", nullable = false)
    @Builder.Default
    private Integer defaultBufferMinutes = 30;

    @Column(name = "min_days_in_advance", nullable = false)
    @Builder.Default
    private Integer minDaysInAdvance = 3;

    @Column(name = "availability_horizon_months", nullable = false)
    @Builder.Default
    private Integer availabilityHorizonMonths = 3;

    @Column(name = "opening_time", nullable = false)
    @Builder.Default
    private LocalTime openingTime = LocalTime.of(8, 0);

    @Column(name = "closing_time", nullable = false)
    @Builder.Default
    private LocalTime closingTime = LocalTime.of(20, 0);

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