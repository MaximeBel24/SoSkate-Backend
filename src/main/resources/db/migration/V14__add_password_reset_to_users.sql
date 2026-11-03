-- Password reset: a short-lived code issued by AuthService.forgotPassword
-- and checked by AuthService.resetPassword.
-- Both columns are NULL when no reset is in progress.
ALTER TABLE users
    ADD COLUMN reset_token VARCHAR(255) NULL,
    ADD COLUMN reset_token_expiry DATETIME(6) NULL;