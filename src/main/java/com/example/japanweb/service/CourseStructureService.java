package com.example.japanweb.service;

import com.example.japanweb.dto.request.course.ChapterRequest;
import com.example.japanweb.dto.request.course.CourseRequest;
import com.example.japanweb.dto.request.course.LessonRequest;
import com.example.japanweb.dto.request.course.SectionRequest;
import com.example.japanweb.dto.response.course.CourseChapterDTO;
import com.example.japanweb.dto.response.course.CourseDTO;
import com.example.japanweb.dto.response.course.CourseLessonDTO;
import com.example.japanweb.dto.response.course.CourseSectionDTO;
import com.example.japanweb.entity.CourseSectionType;

import java.util.List;

public interface CourseStructureService {

    List<CourseDTO> getAllCourses(boolean includeTree);

    CourseDTO getCourseById(Long courseId);

    CourseDTO createCourse(CourseRequest request);

    CourseDTO updateCourse(Long courseId, CourseRequest request);

    void deleteCourse(Long courseId);

    List<CourseChapterDTO> getAllChapters();

    CourseChapterDTO createChapter(Long courseId, ChapterRequest request);

    CourseChapterDTO updateChapter(Long chapterId, ChapterRequest request);

    void deleteChapter(Long chapterId);

    List<CourseSectionDTO> getSections(CourseSectionType type);

    CourseSectionDTO createSection(Long chapterId, SectionRequest request);

    CourseSectionDTO updateSection(Long sectionId, SectionRequest request);

    void deleteSection(Long sectionId);

    List<CourseLessonDTO> getAllLessons();

    CourseLessonDTO createLesson(Long sectionId, LessonRequest request);

    CourseLessonDTO updateLesson(Long lessonId, LessonRequest request);

    void deleteLesson(Long lessonId);
}
