package com.example.japanweb.repository;

import com.example.japanweb.entity.GrammarChapter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrammarChapterRepository extends JpaRepository<GrammarChapter, Long> {

    @EntityGraph(attributePaths = {"grammarPoints", "grammarPoints.examples"})
    Optional<GrammarChapter> findWithDetailsById(Long id);
}
