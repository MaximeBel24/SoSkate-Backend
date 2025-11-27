-- =====================================================
-- SoSkate Database - V1 Clean Migration
-- Version: V1
-- Description: Clean baseline schema from existing entities
-- Author: Maxime
-- Date: 2024-11-24
--
-- IMPORTANT NOTES:
-- - NO event_id in bookings table
-- - NO @ElementCollection tables (spot_photos, instructor_photos)
-- - Photos are POLYMORPHIC via PhotoEntity
-- - Tables with existing data are preserved: users, customers, spots, photos, services
-- =====================================================

-- =====================================================
-- TABLE: users (preserved - has data)
-- Parent table for JOINED inheritance
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(255),
    is_admin BIT(1) DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    INDEX idx_users_email (email),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: customers (preserved - has data)
-- Child table - JOINED inheritance
-- =====================================================
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY,
    birth_date DATE,

    CONSTRAINT fk_customers_users
        FOREIGN KEY (id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: instructors (recreated - no data)
-- Child table - JOINED inheritance
-- =====================================================
CREATE TABLE IF NOT EXISTS instructors (
    id BIGINT PRIMARY KEY,
    status ENUM('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED') NOT NULL DEFAULT 'PENDING',
    bio TEXT,

    CONSTRAINT fk_instructors_users
        FOREIGN KEY (id) REFERENCES users(id)
        ON DELETE CASCADE,

    INDEX idx_instructors_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: spots (preserved - has data)
-- =====================================================
CREATE TABLE IF NOT EXISTS spots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    is_indoor BIT(1) NOT NULL DEFAULT 0,
    is_active BIT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    INDEX idx_spots_location (latitude, longitude),
    INDEX idx_spots_city (city),
    INDEX idx_spots_active (is_active),
    INDEX idx_spots_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: services (preserved - has data)
-- =====================================================
CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    service_type ENUM('EVENT', 'LESSON', 'PRIVATE_COACHING', 'RENTAL', 'SUBSCRIPTION') NOT NULL,
    base_price_cents INT NOT NULL,
    duration_minutes INT,
    is_active BIT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    INDEX idx_services_type (service_type),
    INDEX idx_services_active (is_active),
    INDEX idx_services_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: events (recreated - no data)
-- =====================================================
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    spot_id BIGINT,
    instructor_id BIGINT,
    start_time DATETIME(6),
    stop_time DATETIME(6),
    max_participants INT,

    CONSTRAINT fk_events_spot
        FOREIGN KEY (spot_id) REFERENCES spots(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_events_instructor
        FOREIGN KEY (instructor_id) REFERENCES instructors(id)
        ON DELETE SET NULL,

    INDEX idx_events_spot_id (spot_id),
    INDEX idx_events_instructor_id (instructor_id),
    INDEX idx_events_start_time (start_time),
    INDEX idx_events_stop_time (stop_time),
    INDEX idx_events_time_range (start_time, stop_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: bookings (recreated - no data)
-- IMPORTANT: NO event_id column - no relation to events
-- =====================================================
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT,
    service_id BIGINT,
    sport_id BIGINT COMMENT 'Actually spot_id (typo kept for entity compatibility)',
    start_time DATETIME(6),
    stop_time DATETIME(6),
    status ENUM('CANCELLED', 'COMPLETED', 'CONFIRMED', 'NO_SHOW', 'PENDING') NOT NULL DEFAULT 'PENDING',
    notes TEXT,

    CONSTRAINT fk_bookings_instructor
        FOREIGN KEY (instructor_id) REFERENCES instructors(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_bookings_service
        FOREIGN KEY (service_id) REFERENCES services(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_bookings_spot
        FOREIGN KEY (sport_id) REFERENCES spots(id)
        ON DELETE SET NULL,

    INDEX idx_bookings_instructor_id (instructor_id),
    INDEX idx_bookings_service_id (service_id),
    INDEX idx_bookings_spot_id (sport_id),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_start_time (start_time),
    INDEX idx_bookings_time_range (start_time, stop_time),
    INDEX idx_bookings_status_time (status, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: customer_booking (recreated - no data)
-- Join table between customers and bookings
-- =====================================================
CREATE TABLE IF NOT EXISTS customer_booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,

    CONSTRAINT fk_customer_booking_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_customer_booking_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
        ON DELETE CASCADE,

    UNIQUE KEY uk_customer_booking (customer_id, booking_id),
    INDEX idx_customer_booking_customer_id (customer_id),
    INDEX idx_customer_booking_booking_id (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: customer_event (recreated - no data)
-- Join table between customers and events
-- =====================================================
CREATE TABLE IF NOT EXISTS customer_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,

    CONSTRAINT fk_customer_event_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_customer_event_event
        FOREIGN KEY (event_id) REFERENCES events(id)
        ON DELETE CASCADE,

    UNIQUE KEY uk_customer_event (customer_id, event_id),
    INDEX idx_customer_event_customer_id (customer_id),
    INDEX idx_customer_event_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: event_participants (recreated - no data)
-- @ManyToMany join table for event participants
-- =====================================================
CREATE TABLE IF NOT EXISTS event_participants (
    event_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,

    PRIMARY KEY (event_id, customer_id),

    CONSTRAINT fk_event_participants_event
        FOREIGN KEY (event_id) REFERENCES events(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_event_participants_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE CASCADE,

    INDEX idx_event_participants_event_id (event_id),
    INDEX idx_event_participants_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: comments (recreated - no data)
-- Polymorphic comments for spots, instructors, events
-- =====================================================
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    author VARCHAR(255),
    target_type VARCHAR(255) COMMENT 'Polymorphic: SPOT, INSTRUCTOR, EVENT',
    target_id BIGINT COMMENT 'ID of the target entity',
    content TEXT NOT NULL,
    rating INT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_comments_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE SET NULL,

    INDEX idx_comments_customer_id (customer_id),
    INDEX idx_comments_target (target_type, target_id),
    INDEX idx_comments_rating (rating),
    INDEX idx_comments_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLE: photos (preserved - has data)
-- POLYMORPHIC photo storage for all entities
-- NO @ElementCollection tables (spot_photos, instructor_photos)
-- =====================================================
CREATE TABLE IF NOT EXISTS photos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type ENUM('CUSTOMER', 'EVENT', 'INSTRUCTOR', 'SPOT') NOT NULL,
    entity_id BIGINT NOT NULL,
    photo_type ENUM('AVATAR', 'COVER', 'GALLERY', 'TRICK') NOT NULL,
    url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(255),
    file_size BIGINT NOT NULL COMMENT 'Size in bytes',
    mime_type VARCHAR(50),
    width INT NOT NULL,
    height INT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    uploaded_by BIGINT,
    uploaded_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted BIT(1) NOT NULL DEFAULT 0,
    deleted_at DATETIME(6),
    deleted_by BIGINT,

    INDEX idx_entity_type_id (entity_type, entity_id),
    INDEX idx_photo_type (photo_type),
    INDEX idx_deleted (deleted),
    INDEX idx_photos_active (entity_type, entity_id, photo_type, deleted),
    INDEX idx_photos_gallery (entity_type, entity_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Data verification queries
-- =====================================================

-- Count existing data
SELECT '✓ Counting preserved data...' AS status;

SELECT CONCAT('Users: ', COUNT(*)) AS preserved_data FROM users;
SELECT CONCAT('Customers: ', COUNT(*)) AS preserved_data FROM customers;
SELECT CONCAT('Spots: ', COUNT(*)) AS preserved_data FROM spots;
SELECT CONCAT('Services: ', COUNT(*)) AS preserved_data FROM services;
SELECT CONCAT('Photos: ', COUNT(*)) AS preserved_data FROM photos;

-- List all tables
SELECT '✓ Listing all tables...' AS status;

SELECT
    TABLE_NAME,
    TABLE_ROWS,
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) AS size_mb
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME NOT LIKE 'flyway%'
ORDER BY TABLE_NAME;

-- Verify no orphaned records
SELECT '✓ Verifying data integrity...' AS status;

SELECT
    CASE
        WHEN COUNT(*) = 0 THEN '✓ No orphaned customers'
        ELSE CONCAT('⚠️  ', COUNT(*), ' orphaned customers found')
    END AS integrity_check
FROM customers c
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.id = c.id);

-- =====================================================
-- Summary
-- =====================================================

SELECT '✓✓✓ V1 Migration Complete ✓✓✓' AS status;

SELECT CONCAT(
    '✓ Schema created with ',
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME NOT LIKE 'flyway%'),
    ' tables'
) AS summary;

-- =====================================================
-- END OF V1 BASELINE MIGRATION
-- =====================================================