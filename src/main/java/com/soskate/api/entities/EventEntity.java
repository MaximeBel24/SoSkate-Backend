package com.soskate.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private SpotEntity spot;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private InstructorEntity instructor;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<CustomerEntity> participants = new ArrayList<>();

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<BookingEntity> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerEventEntity> customerEvents = new ArrayList<>();

    public EventEntity(Long id, String title, String description, SpotEntity spot, LocalDateTime startTime, LocalDateTime stopTime, Integer maxParticipants, InstructorEntity instructor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.spot = spot;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.maxParticipants = maxParticipants;
        this.instructor = instructor;
    }
}