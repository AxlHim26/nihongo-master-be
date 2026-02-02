package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.response.grammar.GrammarBookDTO;
import com.example.japanweb.dto.response.grammar.GrammarChapterDetailDTO;
import com.example.japanweb.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GrammarController {

    private final GrammarService grammarService;

    @GetMapping("/grammar-books")
    public ApiResponse<List<GrammarBookDTO>> getAllBooks() {
        List<GrammarBookDTO> books = grammarService.getAllBooks();
        return ApiResponse.success(books);
    }

    @GetMapping("/chapters/{id}")
    public ApiResponse<GrammarChapterDetailDTO> getChapterDetails(@PathVariable Long id) {
        GrammarChapterDetailDTO chapter = grammarService.getChapterDetails(id);
        return ApiResponse.success(chapter);
    }
}
