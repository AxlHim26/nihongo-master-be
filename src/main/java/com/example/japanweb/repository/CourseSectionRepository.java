package com.example.japanweb.repository;

import com.example.japanweb.entity.CourseSection;
import com.example.japanweb.entity.CourseSectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findAllByOrderBySectionOrderAscIdAsc();

    List<CourseSection> findByTypeOrderBySectionOrderAscIdAsc(CourseSectionType type);

    List<CourseSection> findByChapterIdInOrderByChapterIdAscSectionOrderAscIdAsc(Collection<Long> chapterIds);

    long countByChapterId(Long chapterId);
}
