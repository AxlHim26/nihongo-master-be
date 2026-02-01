package com.example.japanweb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a video file stored in Google Drive,
 * linked to a lesson (grammar or vocabulary).
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lesson_videos", indexes = {
        @Index(name = "idx_lesson_videos_lesson", columnList = "lesson_type, lesson_id"),
        @Index(name = "idx_lesson_videos_drive_file", columnList = "drive_file_id")
})
public class LessonVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_file_id", nullable = false, unique = true, length = 100)
    private String driveFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false, length = 20)
    private LessonType lessonType;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(nullable = false, length = 255)
    private String title;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
