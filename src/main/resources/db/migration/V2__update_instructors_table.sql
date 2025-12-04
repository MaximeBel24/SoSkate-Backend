-- =====================================================
-- V2__update_instructors_table.sql
-- Add new fields for instructor management feature
-- =====================================================

-- ==================== Update InstructorStatus Enum ====================
-- Note: MySQL ENUM modification requires recreating the column
-- We'll use VARCHAR to store enum values (more flexible with JPA @Enumerated(STRING))

ALTER TABLE instructors
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'INVITED';

-- ==================== Add Activation Token Fields ====================

ALTER TABLE instructors
    ADD COLUMN activation_token VARCHAR(36) UNIQUE AFTER status;

ALTER TABLE instructors
    ADD COLUMN activation_token_expiry DATETIME AFTER activation_token;

-- ==================== Add Audit Fields ====================

ALTER TABLE instructors
    ADD COLUMN invited_at DATETIME AFTER activation_token_expiry;

ALTER TABLE instructors
    ADD COLUMN activated_at DATETIME AFTER invited_at;

-- ==================== Add Profile Fields ====================

-- Specialty enum stored as string
ALTER TABLE instructors
    ADD COLUMN specialty VARCHAR(20) AFTER bio;

-- Years of experience
ALTER TABLE instructors
    ADD COLUMN years_of_experience INT AFTER specialty;

-- ==================== Add Social Media Fields ====================

ALTER TABLE instructors
    ADD COLUMN instagram_handle VARCHAR(30) AFTER years_of_experience;

ALTER TABLE instructors
    ADD COLUMN youtube_channel VARCHAR(100) AFTER instagram_handle;

-- ==================== Add Indexes for Performance ====================

-- Index on activation_token for fast lookup during activation
CREATE INDEX idx_instructors_activation_token ON instructors(activation_token);

-- Index on specialty for filtering by discipline
CREATE INDEX idx_instructors_specialty ON instructors(specialty);

-- ==================== Add Constraints ====================

-- Ensure years_of_experience is within valid range
ALTER TABLE instructors
    ADD CONSTRAINT chk_years_of_experience
    CHECK (years_of_experience IS NULL OR (years_of_experience >= 0 AND years_of_experience <= 50));