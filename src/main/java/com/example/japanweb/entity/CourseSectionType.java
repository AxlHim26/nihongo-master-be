package com.example.japanweb.entity;

/**
 * Type of chapter content section.
 */
public enum CourseSectionType {
    VOCABULARY,
    GRAMMAR,
    KANJI,
    READING,
    LISTENING;

    public String getVietnameseLabel() {
        return switch (this) {
            case VOCABULARY -> "Từ vựng";
            case GRAMMAR -> "Ngữ pháp";
            case KANJI -> "Hán Tự";
            case READING -> "Đọc";
            case LISTENING -> "Nghe";
        };
    }
}
