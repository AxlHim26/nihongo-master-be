-- V7: Add query-focused indexes for course structure endpoints

CREATE INDEX IF NOT EXISTS idx_course_chapters_order
    ON course_chapters(chapter_order, id);

CREATE INDEX IF NOT EXISTS idx_course_sections_type_order
    ON course_sections(section_type, section_order, id);

CREATE INDEX IF NOT EXISTS idx_course_sections_chapter_order
    ON course_sections(chapter_id, section_order, id);

CREATE INDEX IF NOT EXISTS idx_course_lessons_order
    ON course_lessons(lesson_order, id);
