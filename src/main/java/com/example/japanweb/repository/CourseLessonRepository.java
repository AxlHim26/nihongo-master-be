package com.example.japanweb.repository;

import com.example.japanweb.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, Long> {

    List<CourseLesson> findAllByOrderByLessonOrderAscIdAsc();

    List<CourseLesson> findBySectionIdInOrderBySectionIdAscLessonOrderAscIdAsc(Collection<Long> sectionIds);
}
