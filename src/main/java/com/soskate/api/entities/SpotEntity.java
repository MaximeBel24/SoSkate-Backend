package com.soskate.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "spots")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    private String city;

    private String zipCode;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "is_indoor", nullable = false)
    private Boolean isIndoor;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "spot_photos",
            joinColumns = @JoinColumn(name = "spot_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SpotEntity(String name, String description, String address, String city, String zipCode, BigDecimal latitude, BigDecimal longitude, Boolean isIndoor, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isIndoor = isIndoor;
        this.isActive = isActive;
    }
}
