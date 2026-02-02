package com.example.japanweb.service.impl;

import com.example.japanweb.dto.response.grammar.GrammarBookDTO;
import com.example.japanweb.dto.response.grammar.GrammarChapterDetailDTO;
import com.example.japanweb.entity.GrammarChapter;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.mapper.GrammarMapper;
import com.example.japanweb.repository.GrammarBookRepository;
import com.example.japanweb.repository.GrammarChapterRepository;
import com.example.japanweb.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrammarServiceImpl implements GrammarService {

    private final GrammarBookRepository grammarBookRepository;
    private final GrammarChapterRepository grammarChapterRepository;
    private final GrammarMapper grammarMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GrammarBookDTO> getAllBooks() {
        return grammarBookRepository.findAll().stream()
                .map(grammarMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GrammarChapterDetailDTO getChapterDetails(Long chapterId) {
        GrammarChapter chapter = grammarChapterRepository.findWithDetailsById(chapterId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.GRAMMAR_CHAPTER_NOT_FOUND,
                        "Chapter not found with id: " + chapterId
                ));
        return grammarMapper.toChapterDetailDTO(chapter);
    }
}
