package com.example.japanweb.repository;

import com.example.japanweb.entity.CourseSection;
import com.example.japanweb.entity.CourseSectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByTypeOrderBySectionOrderAsc(CourseSectionType type);
}
