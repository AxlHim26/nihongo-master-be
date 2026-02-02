package com.example.japanweb.repository;

import com.example.japanweb.entity.VocabEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabEntryRepository extends JpaRepository<VocabEntry, Long> {

    @Query("""
            SELECT ve FROM VocabEntry ve
            WHERE ve.course.id = :courseId
              AND ve.randomKey >= :seed
            ORDER BY ve.randomKey ASC
            """)
    List<VocabEntry> findRandomBatchFromSeed(
            @Param("courseId") Long courseId,
            @Param("seed") double seed,
            Pageable pageable
    );

    @Query("""
            SELECT ve FROM VocabEntry ve
            WHERE ve.course.id = :courseId
              AND ve.randomKey < :seed
            ORDER BY ve.randomKey ASC
            """)
    List<VocabEntry> findRandomBatchWrapFromSeed(
            @Param("courseId") Long courseId,
            @Param("seed") double seed,
            Pageable pageable
    );
}
