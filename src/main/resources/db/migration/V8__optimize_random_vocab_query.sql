-- V8: Replace ORDER BY RANDOM() hot path with indexed random-key lookup

ALTER TABLE vocab_entries
    ADD COLUMN IF NOT EXISTS random_key DOUBLE PRECISION;

UPDATE vocab_entries
SET random_key = random()
WHERE random_key IS NULL;

ALTER TABLE vocab_entries
    ALTER COLUMN random_key SET DEFAULT random();

ALTER TABLE vocab_entries
    ALTER COLUMN random_key SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_vocab_entries_course_random_key
    ON vocab_entries(course_id, random_key);
