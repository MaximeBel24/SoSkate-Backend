package com.soskate.api.entities;

import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.SkateSpecialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InstructorEntity extends UserEntity {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InstructorStatus status;

    /**
     * Unique token sent to the instructor for account activation.
     * Generated when admin creates the instructor account.
     * Nullified after successful activation.
     */
    @Column(name = "activation_token", length = 36, unique = true)
    private String activationToken;

    /**
     * Expiration timestamp for the activation token.
     * Configurable via soskate.security.instructor-activation-token-ttl-hours property.
     */
    @Column(name = "activation_token_expiry")
    private LocalDateTime activationTokenExpiry;

    /**
     * Timestamp when the invitation was sent to the instructor.
     */
    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    /**
     * Timestamp when the instructor activated their account.
     */
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    /**
     * Instructor's biography/description visible to customers.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Primary skateboarding discipline the instructor specializes in.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SkateSpecialty specialty;

    /**
     * Number of years of skateboarding/teaching experience.
     */
    @Column(name = "years_of_experience")
    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;

    // ==================== Social Media ====================

    /**
     * Instagram handle (without @).
     * Example: "tony_hawk"
     */
    @Column(name = "instagram_handle", length = 30)
    @Size(max = 30, message = "Instagram handle cannot exceed 30 characters")
    private String instagramHandle;

    /**
     * YouTube channel handle or ID.
     * Example: "tonyhawk" or "UC..."
     */
    @Column(name = "youtube_channel", length = 100)
    @Size(max = 100, message = "YouTube channel cannot exceed 100 characters")
    private String youtubeChannel;

    // ==================== Relationships ====================

    @ToString.Exclude
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingEntity> bookings = new ArrayList<>();

    @Column(name = "buffer_minutes_between_bookings", nullable = false)
    @Builder.Default
    private Integer bufferMinutesBetweenBookings = 30;

    // ==================== Helper Methods ====================

    /**
     * Checks if the activation token is still valid.
     * @return true if token exists and has not expired
     */
    public boolean isActivationTokenValid() {
        return activationToken != null
                && activationTokenExpiry != null
                && LocalDateTime.now().isBefore(activationTokenExpiry);
    }

    /**
     * Checks if the instructor account is activated and usable.
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == InstructorStatus.ACTIVE;
    }

    /**
     * Checks if the instructor can receive bookings.
     * @return true if account is active and not suspended
     */
    public boolean canReceiveBookings() {
        return status == InstructorStatus.ACTIVE;
    }
}