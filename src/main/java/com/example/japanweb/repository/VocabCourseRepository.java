package com.example.japanweb.repository;

import com.example.japanweb.entity.VocabCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabCourseRepository extends JpaRepository<VocabCourse, Long> {
    List<VocabCourse> findTop5ByOrderByCreatedAtDesc();
}
