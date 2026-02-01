package com.example.japanweb.repository;

import com.example.japanweb.entity.LessonType;
import com.example.japanweb.entity.LessonVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LessonVideo entity.
 */
@Repository
public interface LessonVideoRepository extends JpaRepository<LessonVideo, Long> {

    /**
     * Find a video by its Google Drive file ID.
     */
    Optional<LessonVideo> findByDriveFileId(String driveFileId);

    /**
     * Find all videos for a specific lesson.
     */
    List<LessonVideo> findByLessonTypeAndLessonId(LessonType lessonType, Long lessonId);
}
