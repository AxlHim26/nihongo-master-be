package com.example.japanweb.service;

import com.example.japanweb.dto.VocabCourseDTO;
import com.example.japanweb.dto.VocabCourseDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VocabService {
    /**
     * Get latest vocabulary courses.
     */
    List<VocabCourseDTO> getLatestCourses();

    /**
     * Get all vocabulary courses with pagination.
     */
    Page<VocabCourseDTO> getAllCourses(Pageable pageable);

    /**
     * Get course by ID with all vocabulary entries.
     */
    VocabCourseDetailDTO getCourseWithEntries(Long courseId);
}
