-- Bookings now read the blocks: the old table and the 72-hour delay are no longer used.
DROP TABLE availabilities;

ALTER TABLE platform_settings
    DROP COLUMN min_booking_hours_in_advance;
