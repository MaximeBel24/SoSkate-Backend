-- =====================================================
-- SoSkate Database - V5 Move duration to booking
-- Description: Duration is per booking, not per service
-- Author: Maxime
-- Date: 2026-01-14
-- =====================================================

-- Supprimer duration_minutes de services (plus utilisé)
ALTER TABLE services DROP COLUMN duration_minutes;

-- Ajouter duration_minutes à bookings
ALTER TABLE bookings
    ADD COLUMN duration_minutes INT NOT NULL DEFAULT 60 AFTER end_time;

-- Ajouter contrainte sur les valeurs autorisées
ALTER TABLE bookings
    ADD CONSTRAINT chk_duration_minutes
        CHECK (duration_minutes IN (60, 90, 120, 180, 240));