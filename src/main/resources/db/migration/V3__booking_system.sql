-- =====================================================
-- SoSkate Database - V3 Booking System
-- Description: Multi-participant booking system
-- Author: Maxime
-- Date: 2026-01-13
-- =====================================================

-- =====================================================
-- TABLE: platform_settings
-- Singleton configuration table
-- =====================================================
CREATE TABLE platform_settings (
    id BIGINT PRIMARY KEY DEFAULT 1,
    min_booking_hours_in_advance INT NOT NULL DEFAULT 72,
    auto_refund_hours_limit INT NOT NULL DEFAULT 48,
    default_buffer_minutes INT NOT NULL DEFAULT 30,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT chk_single_row CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO platform_settings (id) VALUES (1);

-- =====================================================
-- TABLE: instructor_spots
-- Links instructors to spots where they teach
-- =====================================================
CREATE TABLE instructor_spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    spot_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_instructor_spots_instructor
        FOREIGN KEY (instructor_id) REFERENCES instructors(id)
          ON DELETE CASCADE,
    CONSTRAINT fk_instructor_spots_spot
        FOREIGN KEY (spot_id) REFERENCES spots(id)
          ON DELETE CASCADE,

    UNIQUE KEY uk_instructor_spot (instructor_id, spot_id),
    INDEX idx_instructor_spots_instructor (instructor_id),
    INDEX idx_instructor_spots_spot (spot_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: availabilities
-- Instructor availability time ranges
-- =====================================================
CREATE TABLE availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('AVAILABLE', 'CANCELLED') NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_availabilities_instructor
        FOREIGN KEY (instructor_id) REFERENCES instructors(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_availability_time_order CHECK (end_time > start_time),

    INDEX idx_availabilities_instructor (instructor_id),
    INDEX idx_availabilities_date (date),
    INDEX idx_availabilities_instructor_date (instructor_id, date),
    INDEX idx_availabilities_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- ALTER: instructors - add buffer configuration
-- =====================================================
ALTER TABLE instructors
    ADD COLUMN buffer_minutes_between_bookings INT NOT NULL DEFAULT 30;

-- =====================================================
-- ALTER: customers - add late cancellation tracking
-- =====================================================
ALTER TABLE customers
    ADD COLUMN late_cancellation_count INT NOT NULL DEFAULT 0,
    ADD COLUMN last_late_cancellation DATETIME(6);

-- =====================================================
-- ALTER: services - add max_participants
-- =====================================================
ALTER TABLE services
    ADD COLUMN max_participants INT NOT NULL DEFAULT 1;

-- =====================================================
-- ALTER: bookings - adapt for multi-participant system
-- =====================================================

-- Rename stop_time to end_time
ALTER TABLE bookings
    CHANGE COLUMN stop_time end_time DATETIME(6);

-- Add new columns
ALTER TABLE bookings
    ADD COLUMN max_participants INT NOT NULL DEFAULT 1 AFTER sport_id,
    ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

-- Update status enum
ALTER TABLE bookings
    MODIFY COLUMN status ENUM(
    'OPEN',
    'FULL',
    'CONFIRMED',
    'COMPLETED',
    'CANCELLED',
    -- Legacy
    'PENDING',
    'NO_SHOW'
    ) NOT NULL DEFAULT 'OPEN';

-- =====================================================
-- TABLE: booking_participants
-- Each participant in a booking
-- =====================================================
CREATE TABLE booking_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    status ENUM(
        'INVITED',
        'PENDING_PAYMENT',
        'CONFIRMED',
        'CANCELLED',
        'REFUNDED',
        'COMPLETED',
        'NO_SHOW'
    ) NOT NULL DEFAULT 'PENDING_PAYMENT',
    amount_cents INT,
    payment_intent_id VARCHAR(255),
    invited_by ENUM('SELF', 'INSTRUCTOR', 'PARTICIPANT') NOT NULL DEFAULT 'SELF',
    invited_by_customer_id BIGINT,
    participants_notes VARCHAR(500),
    cancellation_reason VARCHAR(500),
    cancelled_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_booking_participants_booking
      FOREIGN KEY (booking_id) REFERENCES bookings(id)
          ON DELETE CASCADE,
    CONSTRAINT fk_booking_participants_customer
      FOREIGN KEY (customer_id) REFERENCES customers(id)
          ON DELETE CASCADE,
    CONSTRAINT fk_booking_participants_inviter
      FOREIGN KEY (invited_by_customer_id) REFERENCES customers(id)
          ON DELETE SET NULL,

    UNIQUE KEY uk_booking_customer (booking_id, customer_id),
    INDEX idx_booking_participants_booking (booking_id),
    INDEX idx_booking_participants_customer (customer_id),
    INDEX idx_booking_participants_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
SELECT '✓ V3 Booking System Complete' AS status;
-- =====================================================