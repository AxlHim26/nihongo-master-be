-- V14: rename KANJI Vietnamese display label from "Chữ Hán" to "Hán Tự"

UPDATE course_sections
SET title = 'Hán Tự',
    updated_at = CURRENT_TIMESTAMP
WHERE section_type = 'KANJI'
  AND (title = 'Chữ Hán' OR title = 'Chữ hán');
