-- =====================================================
-- SoSkate Database - V4 Add participant count
-- Description: Track number of participants per booking participant
-- Author: Maxime
-- Date: 2026-01-13
-- =====================================================

ALTER TABLE booking_participants
    ADD COLUMN number_of_participants INT NOT NULL DEFAULT 1 AFTER customer_id;