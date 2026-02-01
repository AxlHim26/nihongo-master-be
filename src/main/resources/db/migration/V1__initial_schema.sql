-- V1__initial_schema.sql
-- Japience Database Schema

-- ============================================
-- USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- ============================================
-- GRAMMAR TABLES
-- ============================================
CREATE TABLE IF NOT EXISTS grammar_books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    level VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_grammar_books_level ON grammar_books(level);

CREATE TABLE IF NOT EXISTS grammar_chapters (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES grammar_books(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_grammar_chapters_book ON grammar_chapters(book_id);

CREATE TABLE IF NOT EXISTS grammar_points (
    id BIGSERIAL PRIMARY KEY,
    chapter_id BIGINT NOT NULL REFERENCES grammar_chapters(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    meaning TEXT,
    structure TEXT,
    note TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_grammar_points_chapter ON grammar_points(chapter_id);

CREATE TABLE IF NOT EXISTS grammar_examples (
    id BIGSERIAL PRIMARY KEY,
    grammar_point_id BIGINT NOT NULL REFERENCES grammar_points(id) ON DELETE CASCADE,
    japanese TEXT NOT NULL,
    reading TEXT,
    meaning TEXT,
    display_order INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_grammar_examples_point ON grammar_examples(grammar_point_id);

-- ============================================
-- VOCABULARY TABLES
-- ============================================
CREATE TABLE IF NOT EXISTS vocab_courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    level VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',
    creator_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    total_words INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vocab_courses_level ON vocab_courses(level);
CREATE INDEX idx_vocab_courses_type ON vocab_courses(type);
CREATE INDEX idx_vocab_courses_creator ON vocab_courses(creator_id);

CREATE TABLE IF NOT EXISTS vocab_entries (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES vocab_courses(id) ON DELETE CASCADE,
    term VARCHAR(100) NOT NULL,
    reading VARCHAR(200) NOT NULL,
    meaning TEXT NOT NULL,
    example TEXT,
    level VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vocab_entries_course_term ON vocab_entries(course_id, term);
CREATE INDEX idx_vocab_entries_level ON vocab_entries(level);

-- ============================================
-- USER LEARNING PROGRESS (SRS)
-- ============================================
CREATE TABLE IF NOT EXISTS user_learning_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vocab_id BIGINT NOT NULL REFERENCES vocab_entries(id) ON DELETE CASCADE,
    next_review_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    interval_days INTEGER NOT NULL DEFAULT 0,
    ease_factor DECIMAL(4,2) NOT NULL DEFAULT 2.50,
    repetitions INTEGER NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, vocab_id)
);

CREATE INDEX idx_user_learning_progress_user ON user_learning_progress(user_id);
CREATE INDEX idx_user_learning_progress_next_review ON user_learning_progress(user_id, next_review_at);
