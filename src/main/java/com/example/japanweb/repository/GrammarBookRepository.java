package com.example.japanweb.repository;

import com.example.japanweb.entity.GrammarBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarBookRepository extends JpaRepository<GrammarBook, Long> {
}
