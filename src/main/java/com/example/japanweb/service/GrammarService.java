package com.example.japanweb.service;

import com.example.japanweb.dto.response.grammar.GrammarBookDTO;
import com.example.japanweb.dto.response.grammar.GrammarChapterDetailDTO;

import java.util.List;

public interface GrammarService {
    List<GrammarBookDTO> getAllBooks();
    GrammarChapterDetailDTO getChapterDetails(Long chapterId);
}
