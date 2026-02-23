-- V10: Simplify chapter sections to fixed buckets (GRAMMAR, VOCABULARY, KANJI)

WITH ranked_sections AS (
    SELECT
        id,
        FIRST_VALUE(id) OVER (PARTITION BY chapter_id, section_type ORDER BY id ASC) AS keep_id,
        ROW_NUMBER() OVER (PARTITION BY chapter_id, section_type ORDER BY id ASC) AS rn
    FROM course_sections
), duplicate_sections AS (
    SELECT id, keep_id
    FROM ranked_sections
    WHERE rn > 1
)
UPDATE course_lessons lesson
SET section_id = duplicate_sections.keep_id
FROM duplicate_sections
WHERE lesson.section_id = duplicate_sections.id;

WITH ranked_sections AS (
    SELECT
        id,
        ROW_NUMBER() OVER (PARTITION BY chapter_id, section_type ORDER BY id ASC) AS rn
    FROM course_sections
)
DELETE FROM course_sections
WHERE id IN (
    SELECT id
    FROM ranked_sections
    WHERE rn > 1
);

WITH section_types(section_type, section_order) AS (
    VALUES
        ('GRAMMAR', 1),
        ('VOCABULARY', 2),
        ('KANJI', 3)
)
INSERT INTO course_sections (chapter_id, section_type, title, section_order, created_at, updated_at)
SELECT
    chapter.id,
    section_types.section_type,
    section_types.section_type,
    section_types.section_order,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM course_chapters chapter
CROSS JOIN section_types
LEFT JOIN course_sections existing
    ON existing.chapter_id = chapter.id
    AND existing.section_type = section_types.section_type
WHERE existing.id IS NULL;

UPDATE course_sections
SET title = section_type,
    section_order = CASE section_type
        WHEN 'GRAMMAR' THEN 1
        WHEN 'VOCABULARY' THEN 2
        WHEN 'KANJI' THEN 3
        ELSE section_order
    END,
    updated_at = CURRENT_TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS ux_course_sections_chapter_type
    ON course_sections(chapter_id, section_type);

DROP INDEX IF EXISTS idx_course_sections_type_level;

ALTER TABLE course_sections DROP COLUMN IF EXISTS level;
ALTER TABLE course_sections DROP COLUMN IF EXISTS topic;
ALTER TABLE course_sections DROP COLUMN IF EXISTS status;
