package com.example.japanweb.service.impl;

import com.example.japanweb.dto.VocabCourseDTO;
import com.example.japanweb.dto.VocabCourseDetailDTO;
import com.example.japanweb.dto.VocabEntryDTO;
import com.example.japanweb.entity.VocabCourse;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.mapper.VocabMapper;
import com.example.japanweb.repository.VocabCourseRepository;
import com.example.japanweb.service.VocabService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabServiceImpl implements VocabService {

    private final VocabCourseRepository vocabCourseRepository;
    private final VocabMapper vocabMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard_courses", key = "'latest'")
    public List<VocabCourseDTO> getLatestCourses() {
        return vocabCourseRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(vocabMapper::toCourseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabCourseDTO> getAllCourses(Pageable pageable) {
        return vocabCourseRepository.findAll(pageable)
                .map(vocabMapper::toCourseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public VocabCourseDetailDTO getCourseWithEntries(Long courseId) {
        VocabCourse course = vocabCourseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.VOCAB_COURSE_NOT_FOUND));

        List<VocabEntryDTO> entries = course.getEntries().stream()
                .map(entry -> VocabEntryDTO.builder()
                        .id(entry.getId())
                        .term(entry.getTerm())
                        .reading(entry.getReading())
                        .meaning(entry.getMeaning())
                        .example(entry.getExample())
                        .level(entry.getLevel())
                        .build())
                .collect(Collectors.toList());

        return VocabCourseDetailDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .level(course.getLevel())
                .type(course.getType().name())
                .totalWords(course.getTotalWords())
                .entries(entries)
                .build();
    }
}
