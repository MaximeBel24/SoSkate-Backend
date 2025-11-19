package com.soskate.api.entity;

import com.soskate.api.enums.InstructorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstructorEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstructorStatus status;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection
    @CollectionTable(name = "instructor_photos",
            joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "photo_url")
    private List<String> photo = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingEntity> bookings = new ArrayList<>();
}
