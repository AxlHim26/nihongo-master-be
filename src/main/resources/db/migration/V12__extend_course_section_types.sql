-- V12: allow READING and LISTENING section types

ALTER TABLE course_sections
    DROP CONSTRAINT IF EXISTS course_sections_section_type_check;

ALTER TABLE course_sections
    DROP CONSTRAINT IF EXISTS ck_course_sections_section_type;

ALTER TABLE course_sections
    ADD CONSTRAINT ck_course_sections_section_type
    CHECK (section_type IN ('VOCABULARY', 'GRAMMAR', 'KANJI', 'READING', 'LISTENING'));

WITH section_types(section_type, section_order) AS (
    VALUES
        ('READING', 4),
        ('LISTENING', 5)
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
