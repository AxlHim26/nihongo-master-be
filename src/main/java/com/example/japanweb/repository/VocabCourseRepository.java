package com.example.japanweb.repository;

import com.example.japanweb.entity.VocabCourse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabCourseRepository extends JpaRepository<VocabCourse, Long> {
    List<VocabCourse> findTop5ByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"entries"})
    Optional<VocabCourse> findWithEntriesById(Long id);
}
