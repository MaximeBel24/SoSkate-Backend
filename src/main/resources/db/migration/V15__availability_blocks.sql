-- Availabilities become 15-minute blocks; instructors and clients keep seeing ranges.
-- The old availabilities table stays until bookings read the blocks.
CREATE TABLE availability_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instructor_id BIGINT NOT NULL,
    start_at DATETIME NOT NULL,
    booking_id BIGINT NULL COMMENT 'Session occupying the block, NULL while it is free',

    CONSTRAINT fk_availability_blocks_instructor
        FOREIGN KEY (instructor_id) REFERENCES instructors(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_availability_blocks_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
            ON DELETE SET NULL,

    CONSTRAINT chk_availability_blocks_quarter_hour
        CHECK (MINUTE(start_at) IN (0, 15, 30, 45) AND SECOND(start_at) = 0),

    CONSTRAINT uk_availability_blocks_instructor_start
        UNIQUE (instructor_id, start_at),

    INDEX idx_availability_blocks_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE platform_settings
    ADD COLUMN min_days_in_advance INT NOT NULL DEFAULT 3,
    ADD COLUMN availability_horizon_months INT NOT NULL DEFAULT 3,
    ADD COLUMN opening_time TIME NOT NULL DEFAULT '08:00:00',
    ADD COLUMN closing_time TIME NOT NULL DEFAULT '20:00:00',
    ADD CONSTRAINT chk_platform_settings_opening_hours
        CHECK (opening_time >= '05:00:00' AND opening_time < closing_time);
