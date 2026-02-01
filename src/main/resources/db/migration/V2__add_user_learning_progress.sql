-- V2: Add User Learning Progress table for SRS tracking
-- This table was added to support the SM-2 Spaced Repetition algorithm

CREATE TABLE IF NOT EXISTS user_learning_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vocab_id BIGINT NOT NULL REFERENCES vocab_entries(id) ON DELETE CASCADE,
    next_review_at TIMESTAMP NOT NULL DEFAULT NOW(),
    interval_days INTEGER NOT NULL DEFAULT 1,
    ease_factor DECIMAL(4,2) NOT NULL DEFAULT 2.50,
    repetitions INTEGER NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Each user can only have one progress record per vocab entry
    CONSTRAINT uk_user_vocab UNIQUE (user_id, vocab_id)
);

-- Index for finding due reviews (queries by user_id + next_review_at)
CREATE INDEX IF NOT EXISTS idx_ulp_user_next_review ON user_learning_progress(user_id, next_review_at);

-- Index for user statistics queries
CREATE INDEX IF NOT EXISTS idx_ulp_user_id ON user_learning_progress(user_id);
