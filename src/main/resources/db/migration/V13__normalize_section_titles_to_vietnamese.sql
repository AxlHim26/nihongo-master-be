-- V13: normalize section titles to Vietnamese labels by section type

UPDATE course_sections
SET title = CASE section_type
    WHEN 'VOCABULARY' THEN 'Từ vựng'
    WHEN 'GRAMMAR' THEN 'Ngữ pháp'
    WHEN 'KANJI' THEN 'Chữ Hán'
    WHEN 'READING' THEN 'Đọc'
    WHEN 'LISTENING' THEN 'Nghe'
    ELSE title
END,
updated_at = CURRENT_TIMESTAMP;
