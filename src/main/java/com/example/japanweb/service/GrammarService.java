package com.example.japanweb.service;

import com.example.japanweb.dto.GrammarBookDTO;
import com.example.japanweb.dto.GrammarChapterDetailDTO;

import java.util.List;

public interface GrammarService {
    List<GrammarBookDTO> getAllBooks();
    GrammarChapterDetailDTO getChapterDetails(Long chapterId);
}
