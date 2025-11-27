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

    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerBookingEntity> customerBookings = new ArrayList<>();

    // Méthode utilitaire
//    public int getAvailablePlaces() {
//        int maxPlaces = service.getMaxParticipants();
//        long reservedPlaces = customerBookings.stream()
//                .filter(cb -> cb.getStatus() == CustomerBookingStatus.CONFIRMED)
//                .count();
//        return maxPlaces - (int) reservedPlaces;
//    }
//
//    public boolean isFull() {
//        return getAvailablePlaces() <= 0;
//    }
}
