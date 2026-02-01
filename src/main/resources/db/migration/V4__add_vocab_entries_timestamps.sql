-- V4: Add timestamps to vocab_entries table
ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
