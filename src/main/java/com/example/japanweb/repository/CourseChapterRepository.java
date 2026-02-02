package com.example.japanweb.repository;

import com.example.japanweb.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Long> {

    List<CourseChapter> findAllByOrderByChapterOrderAscIdAsc();

    List<CourseChapter> findByCourseIdInOrderByCourseIdAscChapterOrderAscIdAsc(Collection<Long> courseIds);
}
