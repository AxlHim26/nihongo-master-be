package com.example.japanweb.controller;

import com.example.japanweb.dto.ApiResponse;
import com.example.japanweb.dto.VocabCourseDTO;
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
