package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.response.vocab.VocabCourseDTO;
import com.example.japanweb.service.VocabService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VocabService vocabService;

    @GetMapping("/latest")
    public ApiResponse<List<VocabCourseDTO>> getLatestCourses() {
        List<VocabCourseDTO> courses = vocabService.getLatestCourses();
        return ApiResponse.success(courses);
    }
}
