package com.example.japanweb.controller;

import com.example.japanweb.dto.common.ApiResponse;
import com.example.japanweb.dto.request.course.ChapterRequest;
import com.example.japanweb.dto.request.course.CourseRequest;
import com.example.japanweb.dto.request.course.LessonRequest;
import com.example.japanweb.dto.request.course.SectionRequest;
import com.example.japanweb.dto.response.course.CourseChapterDTO;
import com.example.japanweb.dto.response.course.CourseDTO;
import com.example.japanweb.dto.response.course.CourseLessonDTO;
import com.example.japanweb.dto.response.course.CourseSectionDTO;
import com.example.japanweb.entity.CourseSectionType;
import com.example.japanweb.service.CourseStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only APIs for managing Course -> Chapter -> Section -> Lesson tree.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CourseStructureController {

    private final CourseStructureService courseStructureService;

    @GetMapping("/courses")
    public ApiResponse<List<CourseDTO>> getCourses(@RequestParam(defaultValue = "true") boolean tree) {
        return ApiResponse.success(courseStructureService.getAllCourses(tree));
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<CourseDTO> getCourseById(@PathVariable Long id) {
        return ApiResponse.success(courseStructureService.getCourseById(id));
    }

    @PostMapping("/courses")
    public ApiResponse<CourseDTO> createCourse(@Valid @RequestBody CourseRequest request) {
        return ApiResponse.created(courseStructureService.createCourse(request));
    }

    @PutMapping("/courses/{id}")
    public ApiResponse<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ApiResponse.success(courseStructureService.updateCourse(id, request));
    }

    @DeleteMapping("/courses/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Long id) {
        courseStructureService.deleteCourse(id);
        return ApiResponse.success(null, "Course deleted successfully");
    }

    @GetMapping("/chapters")
    public ApiResponse<List<CourseChapterDTO>> getChapters() {
        return ApiResponse.success(courseStructureService.getAllChapters());
    }

    @PostMapping("/courses/{id}/chapters")
    public ApiResponse<CourseChapterDTO> createChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequest request) {
        return ApiResponse.created(courseStructureService.createChapter(id, request));
    }

    @PutMapping("/chapters/{id}")
    public ApiResponse<CourseChapterDTO> updateChapter(@PathVariable Long id, @Valid @RequestBody ChapterRequest request) {
        return ApiResponse.success(courseStructureService.updateChapter(id, request));
    }

    @DeleteMapping("/chapters/{id}")
    public ApiResponse<Void> deleteChapter(@PathVariable Long id) {
        courseStructureService.deleteChapter(id);
        return ApiResponse.success(null, "Chapter deleted successfully");
    }

    @GetMapping("/sections")
    public ApiResponse<List<CourseSectionDTO>> getSections(
            @RequestParam(required = false) CourseSectionType type) {
        return ApiResponse.success(courseStructureService.getSections(type));
    }

    @PostMapping("/chapters/{id}/sections")
    public ApiResponse<CourseSectionDTO> createSection(@PathVariable Long id, @Valid @RequestBody SectionRequest request) {
        return ApiResponse.created(courseStructureService.createSection(id, request));
    }

    @PutMapping("/sections/{id}")
    public ApiResponse<CourseSectionDTO> updateSection(@PathVariable Long id, @Valid @RequestBody SectionRequest request) {
        return ApiResponse.success(courseStructureService.updateSection(id, request));
    }

    @DeleteMapping("/sections/{id}")
    public ApiResponse<Void> deleteSection(@PathVariable Long id) {
        courseStructureService.deleteSection(id);
        return ApiResponse.success(null, "Section deleted successfully");
    }

    @GetMapping("/lessons")
    public ApiResponse<List<CourseLessonDTO>> getLessons() {
        return ApiResponse.success(courseStructureService.getAllLessons());
    }

    @PostMapping("/sections/{id}/lessons")
    public ApiResponse<CourseLessonDTO> createLesson(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        return ApiResponse.created(courseStructureService.createLesson(id, request));
    }

    @PutMapping("/lessons/{id}")
    public ApiResponse<CourseLessonDTO> updateLesson(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        return ApiResponse.success(courseStructureService.updateLesson(id, request));
    }

    @DeleteMapping("/lessons/{id}")
    public ApiResponse<Void> deleteLesson(@PathVariable Long id) {
        courseStructureService.deleteLesson(id);
        return ApiResponse.success(null, "Lesson deleted successfully");
    }
}
