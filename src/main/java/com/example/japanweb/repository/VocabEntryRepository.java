package com.example.japanweb.repository;

import com.example.japanweb.entity.VocabEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabEntryRepository extends JpaRepository<VocabEntry, Long> {

    @Query(value = "SELECT * FROM vocab_entries WHERE course_id = :courseId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<VocabEntry> findRandomEntriesByCourseId(Long courseId, int limit);
}
