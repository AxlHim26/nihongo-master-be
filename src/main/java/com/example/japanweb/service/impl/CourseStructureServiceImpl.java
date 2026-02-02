package com.example.japanweb.service.impl;

import com.example.japanweb.dto.request.course.ChapterRequest;
import com.example.japanweb.dto.request.course.CourseRequest;
import com.example.japanweb.dto.request.course.LessonRequest;
import com.example.japanweb.dto.request.course.SectionRequest;
import com.example.japanweb.dto.response.course.CourseChapterDTO;
import com.example.japanweb.dto.response.course.CourseDTO;
import com.example.japanweb.dto.response.course.CourseLessonDTO;
import com.example.japanweb.dto.response.course.CourseSectionDTO;
import com.example.japanweb.entity.*;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.CourseChapterRepository;
import com.example.japanweb.repository.CourseLessonRepository;
import com.example.japanweb.repository.CourseRepository;
import com.example.japanweb.repository.CourseSectionRepository;
import com.example.japanweb.service.CourseStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseStructureServiceImpl implements CourseStructureService {

    private final CourseRepository courseRepository;
    private final CourseChapterRepository chapterRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses(boolean includeTree) {
        return courseRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(course -> toCourseDTO(course, includeTree))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        return toCourseDTO(course, true);
    }

    @Override
    @Transactional
    public CourseDTO createCourse(CourseRequest request) {
        Course course = Course.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();

        return toCourseDTO(courseRepository.save(course), true);
    }

    @Override
    @Transactional
    public CourseDTO updateCourse(Long courseId, CourseRequest request) {
        Course course = getCourseOrThrow(courseId);
        course.setName(request.getName().trim());
        course.setDescription(request.getDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());

        return toCourseDTO(courseRepository.save(course), true);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseChapterDTO> getAllChapters() {
        return chapterRepository.findAll(Sort.by(Sort.Direction.ASC, "chapterOrder")).stream()
                .map(chapter -> toChapterDTO(chapter, false))
                .toList();
    }

    @Override
    @Transactional
    public CourseChapterDTO createChapter(Long courseId, ChapterRequest request) {
        Course course = getCourseOrThrow(courseId);

        CourseChapter chapter = CourseChapter.builder()
                .course(course)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .chapterOrder(request.getChapterOrder() == null ? 0 : request.getChapterOrder())
                .build();

        return toChapterDTO(chapterRepository.save(chapter), true);
    }

    @Override
    @Transactional
    public CourseChapterDTO updateChapter(Long chapterId, ChapterRequest request) {
        CourseChapter chapter = getChapterOrThrow(chapterId);

        chapter.setTitle(request.getTitle().trim());
        chapter.setDescription(request.getDescription());
        chapter.setChapterOrder(request.getChapterOrder() == null ? 0 : request.getChapterOrder());

        return toChapterDTO(chapterRepository.save(chapter), true);
    }

    @Override
    @Transactional
    public void deleteChapter(Long chapterId) {
        CourseChapter chapter = getChapterOrThrow(chapterId);
        chapterRepository.delete(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSectionDTO> getSections(CourseSectionType type) {
        List<CourseSection> sections = type == null
                ? sectionRepository.findAll(Sort.by(Sort.Direction.ASC, "sectionOrder"))
                : sectionRepository.findByTypeOrderBySectionOrderAsc(type);

        return sections.stream().map(section -> toSectionDTO(section, false)).toList();
    }

    @Override
    @Transactional
    public CourseSectionDTO createSection(Long chapterId, SectionRequest request) {
        CourseChapter chapter = getChapterOrThrow(chapterId);

        CourseSection section = CourseSection.builder()
                .chapter(chapter)
                .type(request.getType())
                .title(request.getTitle().trim())
                .level(request.getLevel())
                .topic(request.getTopic())
                .status(request.getStatus() == null ? CourseSectionStatus.ACTIVE : request.getStatus())
                .sectionOrder(request.getSectionOrder() == null ? 0 : request.getSectionOrder())
                .build();

        return toSectionDTO(sectionRepository.save(section), true);
    }

    @Override
    @Transactional
    public CourseSectionDTO updateSection(Long sectionId, SectionRequest request) {
        CourseSection section = getSectionOrThrow(sectionId);

        section.setType(request.getType());
        section.setTitle(request.getTitle().trim());
        section.setLevel(request.getLevel());
        section.setTopic(request.getTopic());
        section.setStatus(request.getStatus() == null ? CourseSectionStatus.ACTIVE : request.getStatus());
        section.setSectionOrder(request.getSectionOrder() == null ? 0 : request.getSectionOrder());

        return toSectionDTO(sectionRepository.save(section), true);
    }

    @Override
    @Transactional
    public void deleteSection(Long sectionId) {
        CourseSection section = getSectionOrThrow(sectionId);
        sectionRepository.delete(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseLessonDTO> getAllLessons() {
        return lessonRepository.findAll(Sort.by(Sort.Direction.ASC, "lessonOrder")).stream()
                .map(this::toLessonDTO)
                .toList();
    }

    @Override
    @Transactional
    public CourseLessonDTO createLesson(Long sectionId, LessonRequest request) {
        CourseSection section = getSectionOrThrow(sectionId);

        CourseLesson lesson = CourseLesson.builder()
                .section(section)
                .title(request.getTitle().trim())
                .videoUrl(request.getVideoUrl())
                .pdfUrl(request.getPdfUrl())
                .lessonOrder(request.getLessonOrder() == null ? 0 : request.getLessonOrder())
                .build();

        return toLessonDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public CourseLessonDTO updateLesson(Long lessonId, LessonRequest request) {
        CourseLesson lesson = getLessonOrThrow(lessonId);

        lesson.setTitle(request.getTitle().trim());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setPdfUrl(request.getPdfUrl());
        lesson.setLessonOrder(request.getLessonOrder() == null ? 0 : request.getLessonOrder());

        return toLessonDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        CourseLesson lesson = getLessonOrThrow(lessonId);
        lessonRepository.delete(lesson);
    }

    private Course getCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_FOUND,
                        "Course not found with id: " + courseId));
    }

    private CourseChapter getChapterOrThrow(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_CHAPTER_NOT_FOUND,
                        "Chapter not found with id: " + chapterId));
    }

    private CourseSection getSectionOrThrow(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_SECTION_NOT_FOUND,
                        "Section not found with id: " + sectionId));
    }

    private CourseLesson getLessonOrThrow(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException(ErrorCode.COURSE_LESSON_NOT_FOUND,
                        "Lesson not found with id: " + lessonId));
    }

    private CourseDTO toCourseDTO(Course course, boolean includeTree) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .chapters(includeTree
                        ? course.getChapters().stream()
                                .sorted(Comparator.comparing(CourseChapter::getChapterOrder))
                                .map(chapter -> toChapterDTO(chapter, true))
                                .toList()
                        : List.of())
                .build();
    }

    private CourseChapterDTO toChapterDTO(CourseChapter chapter, boolean includeSections) {
        return CourseChapterDTO.builder()
                .id(chapter.getId())
                .courseId(chapter.getCourse().getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .chapterOrder(chapter.getChapterOrder())
                .sections(includeSections
                        ? chapter.getSections().stream()
                                .sorted(Comparator.comparing(CourseSection::getSectionOrder))
                                .map(section -> toSectionDTO(section, true))
                                .toList()
                        : List.of())
                .build();
    }

    private CourseSectionDTO toSectionDTO(CourseSection section, boolean includeLessons) {
        return CourseSectionDTO.builder()
                .id(section.getId())
                .chapterId(section.getChapter().getId())
                .type(section.getType())
                .title(section.getTitle())
                .level(section.getLevel())
                .topic(section.getTopic())
                .status(section.getStatus())
                .sectionOrder(section.getSectionOrder())
                .lessons(includeLessons
                        ? section.getLessons().stream()
                                .sorted(Comparator.comparing(CourseLesson::getLessonOrder))
                                .map(this::toLessonDTO)
                                .toList()
                        : List.of())
                .build();
    }

    private CourseLessonDTO toLessonDTO(CourseLesson lesson) {
        return CourseLessonDTO.builder()
                .id(lesson.getId())
                .sectionId(lesson.getSection().getId())
                .title(lesson.getTitle())
                .videoUrl(lesson.getVideoUrl())
                .pdfUrl(lesson.getPdfUrl())
                .lessonOrder(lesson.getLessonOrder())
                .build();
    }
}
