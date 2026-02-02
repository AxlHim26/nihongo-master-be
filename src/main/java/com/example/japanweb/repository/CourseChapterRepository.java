package com.example.japanweb.repository;

import com.example.japanweb.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Long> {
}
