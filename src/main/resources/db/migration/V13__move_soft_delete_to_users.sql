-- Soft delete now lives on the parent table: the `deleted` field moved from
-- InstructorEntity to UserEntity, so it applies to customers as well.
-- V6 created these columns on `instructors`; this migration moves them to `users`.

-- 1. Add the columns to `users`.
--    NOT NULL + DEFAULT 0: every existing user, and any row inserted by hand,
--    is "not deleted" unless stated otherwise.
ALTER TABLE users
    ADD COLUMN deleted BIT(1) NOT NULL DEFAULT 0,
    ADD COLUMN deleted_at DATETIME(6) NULL;

-- 2. Copy the existing values from `instructors` (it shares its id with `users`).
--    Customers have no row there and keep the default.
UPDATE users u
    JOIN instructors i ON i.id = u.id
    SET u.deleted = i.deleted,
        u.deleted_at = i.deleted_at;

-- 3. Drop the V6 columns: their values are now in `users` and no code reads them.
ALTER TABLE instructors
    DROP COLUMN deleted,
    DROP COLUMN deleted_at;