package com.soskate.api.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerEntity extends UserEntity {

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerEventEntity> events = new ArrayList<>();

    @Column(name = "late_cancellation_count", nullable = false)
    @Builder.Default
    private Integer lateCancellationCount = 0;

    @Column(name = "last_late_cancellation")
    private LocalDateTime lastLateCancellation;

    public void incrementLateCancellation() {
        this.lateCancellationCount++;
        this.lastLateCancellation = LocalDateTime.now();
    }
}
