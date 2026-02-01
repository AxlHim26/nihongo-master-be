-- V5__add_lesson_videos_table.sql
-- Table for storing video metadata linked to Google Drive files

CREATE TABLE IF NOT EXISTS lesson_videos (
    id BIGSERIAL PRIMARY KEY,
    drive_file_id VARCHAR(100) NOT NULL UNIQUE,
    lesson_type VARCHAR(20) NOT NULL CHECK (lesson_type IN ('GRAMMAR', 'VOCAB')),
    lesson_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lesson_videos_lesson ON lesson_videos(lesson_type, lesson_id);
CREATE INDEX idx_lesson_videos_drive_file ON lesson_videos(drive_file_id);
