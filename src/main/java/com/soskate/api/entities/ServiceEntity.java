package com.soskate.api.entities;

import com.soskate.api.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private ServiceType type;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(name = "base_price_cents", nullable = false)
    private Integer basePriceCents;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public ServiceEntity(String name, ServiceType type, String description, Integer durationMin, Integer basePriceCents, Boolean isActive) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.durationMin = durationMin;
        this.basePriceCents = basePriceCents;
        this.isActive = isActive;
    }
}
