-- V3: Add missing columns to existing tables
-- These columns were added to entities but not present in the pre-existing database

-- Add missing columns to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Add unique constraint on email if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'users_email_key' OR conname = 'uk_users_email'
    ) THEN
        -- Set email for existing users to username@example.com if null
        UPDATE users SET email = username || '@example.com' WHERE email IS NULL;
        -- Make email NOT NULL and add unique constraint
        ALTER TABLE users ALTER COLUMN email SET NOT NULL;
        ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
    END IF;
END $$;

-- Add missing columns to vocab_entries table
ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS example TEXT;
ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS level VARCHAR(10);

-- Add index on level if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_vocab_entries_level ON vocab_entries(level);
