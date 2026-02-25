-- V11: allow free section creation with custom title (remove unique chapter/type constraint)

DROP INDEX IF EXISTS ux_course_sections_chapter_type;
