package com.example.japanweb.service.impl;

import com.example.japanweb.dto.request.course.ChapterRequest;
import com.example.japanweb.dto.request.course.CourseRequest;
import com.example.japanweb.dto.request.course.LessonRequest;
import com.example.japanweb.dto.request.course.SectionRequest;
import com.example.japanweb.dto.response.course.CourseChapterDTO;
import com.example.japanweb.dto.response.course.CourseDTO;
import com.example.japanweb.dto.response.course.CourseLessonDTO;
import com.example.japanweb.dto.response.course.CourseSectionDTO;
import com.example.japanweb.entity.Course;
import com.example.japanweb.entity.CourseChapter;
import com.example.japanweb.entity.CourseLesson;
import com.example.japanweb.entity.CourseSection;
import com.example.japanweb.entity.CourseSectionStatus;
import com.example.japanweb.entity.CourseSectionType;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.CourseChapterRepository;
import com.example.japanweb.repository.CourseLessonRepository;
import com.example.japanweb.repository.CourseRepository;
import com.example.japanweb.repository.CourseSectionRepository;
import com.example.japanweb.service.CourseStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseStructureServiceImpl implements CourseStructureService {

    private static final String CACHE_COURSES = "course_structure_courses";
    private static final String CACHE_COURSE_BY_ID = "course_structure_course_by_id";
    private static final String CACHE_CHAPTERS = "course_structure_chapters";
    private static final String CACHE_SECTIONS = "course_structure_sections";
    private static final String CACHE_LESSONS = "course_structure_lessons";

    private final CourseRepository courseRepository;
    private final CourseChapterRepository chapterRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_COURSES, key = "'tree=' + #includeTree")
    public List<CourseDTO> getAllCourses(boolean includeTree) {
        List<Course> courses = courseRepository.findAllByOrderByCreatedAtDesc();
        return toCourseDTOs(courses, includeTree);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_COURSE_BY_ID, key = "#courseId")
    public CourseDTO getCourseById(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        return toCourseDTOs(List.of(course), true).getFirst();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseDTO createCourse(CourseRequest request) {
        Course course = Course.builder()
                .name(requiredText(request.getName()))
                .description(normalizeText(request.getDescription()))
                .thumbnailUrl(normalizeText(request.getThumbnailUrl()))
                .build();

        Course saved = courseRepository.save(course);
        return getCourseById(saved.getId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseDTO updateCourse(Long courseId, CourseRequest request) {
        Course course = getCourseOrThrow(courseId);
        course.setName(requiredText(request.getName()));
        course.setDescription(normalizeText(request.getDescription()));
        course.setThumbnailUrl(normalizeText(request.getThumbnailUrl()));

        Course saved = courseRepository.save(course);
        return getCourseById(saved.getId());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public void deleteCourse(Long courseId) {
        Course course = getCourseOrThrow(courseId);
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_CHAPTERS, key = "'all'")
    public List<CourseChapterDTO> getAllChapters() {
        return chapterRepository.findAllByOrderByChapterOrderAscIdAsc().stream()
                .map(chapter -> toChapterDTO(chapter, List.of()))
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseChapterDTO createChapter(Long courseId, ChapterRequest request) {
        Course course = getCourseOrThrow(courseId);

        CourseChapter chapter = CourseChapter.builder()
                .course(course)
                .title(requiredText(request.getTitle()))
                .description(normalizeText(request.getDescription()))
                .chapterOrder(request.getChapterOrder() == null ? 0 : request.getChapterOrder())
                .build();

        CourseChapter saved = chapterRepository.save(chapter);
        return toChapterDTO(saved, List.of());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseChapterDTO updateChapter(Long chapterId, ChapterRequest request) {
        CourseChapter chapter = getChapterOrThrow(chapterId);

        chapter.setTitle(requiredText(request.getTitle()));
        chapter.setDescription(normalizeText(request.getDescription()));
        chapter.setChapterOrder(request.getChapterOrder() == null ? 0 : request.getChapterOrder());

        CourseChapter saved = chapterRepository.save(chapter);
        Map<Long, List<CourseSectionDTO>> sectionsByChapter = buildSectionsByChapterId(List.of(saved.getId()));
        return toChapterDTO(saved, sectionsByChapter.getOrDefault(saved.getId(), List.of()));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public void deleteChapter(Long chapterId) {
        CourseChapter chapter = getChapterOrThrow(chapterId);
        chapterRepository.delete(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_SECTIONS, key = "#type == null ? 'all' : #type.name()")
    public List<CourseSectionDTO> getSections(CourseSectionType type) {
        List<CourseSection> sections = type == null
                ? sectionRepository.findAllByOrderBySectionOrderAscIdAsc()
                : sectionRepository.findByTypeOrderBySectionOrderAscIdAsc(type);

        return sections.stream()
                .map(section -> toSectionDTO(section, List.of()))
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseSectionDTO createSection(Long chapterId, SectionRequest request) {
        CourseChapter chapter = getChapterOrThrow(chapterId);

        CourseSection section = CourseSection.builder()
                .chapter(chapter)
                .type(request.getType())
                .title(requiredText(request.getTitle()))
                .level(normalizeText(request.getLevel()))
                .topic(normalizeText(request.getTopic()))
                .status(request.getStatus() == null ? CourseSectionStatus.ACTIVE : request.getStatus())
                .sectionOrder(request.getSectionOrder() == null ? 0 : request.getSectionOrder())
                .build();

        CourseSection saved = sectionRepository.save(section);
        return toSectionDTO(saved, List.of());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseSectionDTO updateSection(Long sectionId, SectionRequest request) {
        CourseSection section = getSectionOrThrow(sectionId);

        section.setType(request.getType());
        section.setTitle(requiredText(request.getTitle()));
        section.setLevel(normalizeText(request.getLevel()));
        section.setTopic(normalizeText(request.getTopic()));
        section.setStatus(request.getStatus() == null ? CourseSectionStatus.ACTIVE : request.getStatus());
        section.setSectionOrder(request.getSectionOrder() == null ? 0 : request.getSectionOrder());

        CourseSection saved = sectionRepository.save(section);
        Map<Long, List<CourseLessonDTO>> lessonsBySection = buildLessonsBySectionId(List.of(saved.getId()));
        return toSectionDTO(saved, lessonsBySection.getOrDefault(saved.getId(), List.of()));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public void deleteSection(Long sectionId) {
        CourseSection section = getSectionOrThrow(sectionId);
        sectionRepository.delete(section);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_LESSONS, key = "'all'")
    public List<CourseLessonDTO> getAllLessons() {
        return lessonRepository.findAllByOrderByLessonOrderAscIdAsc().stream()
                .map(this::toLessonDTO)
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseLessonDTO createLesson(Long sectionId, LessonRequest request) {
        CourseSection section = getSectionOrThrow(sectionId);

        CourseLesson lesson = CourseLesson.builder()
                .section(section)
                .title(requiredText(request.getTitle()))
                .videoUrl(normalizeText(request.getVideoUrl()))
                .pdfUrl(normalizeText(request.getPdfUrl()))
                .lessonOrder(request.getLessonOrder() == null ? 0 : request.getLessonOrder())
                .build();

        return toLessonDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
    public CourseLessonDTO updateLesson(Long lessonId, LessonRequest request) {
        CourseLesson lesson = getLessonOrThrow(lessonId);

        lesson.setTitle(requiredText(request.getTitle()));
        lesson.setVideoUrl(normalizeText(request.getVideoUrl()));
        lesson.setPdfUrl(normalizeText(request.getPdfUrl()));
        lesson.setLessonOrder(request.getLessonOrder() == null ? 0 : request.getLessonOrder());

        return toLessonDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CACHE_COURSES, allEntries = true),
            @CacheEvict(cacheNames = CACHE_COURSE_BY_ID, allEntries = true),
            @CacheEvict(cacheNames = CACHE_CHAPTERS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_SECTIONS, allEntries = true),
            @CacheEvict(cacheNames = CACHE_LESSONS, allEntries = true)
    })
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

    private List<CourseDTO> toCourseDTOs(List<Course> courses, boolean includeTree) {
        if (!includeTree) {
            return courses.stream()
                    .map(course -> toCourseDTO(course, List.of()))
                    .toList();
        }

        Map<Long, List<CourseChapterDTO>> chaptersByCourse = buildChaptersByCourseId(
                courses.stream().map(Course::getId).toList()
        );

        return courses.stream()
                .map(course -> toCourseDTO(course, chaptersByCourse.getOrDefault(course.getId(), List.of())))
                .toList();
    }

    private Map<Long, List<CourseChapterDTO>> buildChaptersByCourseId(List<Long> courseIds) {
        if (courseIds.isEmpty()) {
            return Map.of();
        }

        List<CourseChapter> chapters = chapterRepository.findByCourseIdInOrderByCourseIdAscChapterOrderAscIdAsc(
                courseIds
        );
        if (chapters.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<CourseSectionDTO>> sectionsByChapter = buildSectionsByChapterId(
                chapters.stream().map(CourseChapter::getId).toList()
        );

        Map<Long, List<CourseChapterDTO>> chaptersByCourse = new LinkedHashMap<>();
        for (CourseChapter chapter : chapters) {
            List<CourseSectionDTO> sections = sectionsByChapter.getOrDefault(chapter.getId(), List.of());
            chaptersByCourse.computeIfAbsent(chapter.getCourse().getId(), ignored -> new ArrayList<>())
                    .add(toChapterDTO(chapter, sections));
        }
        return chaptersByCourse;
    }

    private Map<Long, List<CourseSectionDTO>> buildSectionsByChapterId(List<Long> chapterIds) {
        if (chapterIds.isEmpty()) {
            return Map.of();
        }

        List<CourseSection> sections = sectionRepository.findByChapterIdInOrderByChapterIdAscSectionOrderAscIdAsc(
                chapterIds
        );
        if (sections.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<CourseLessonDTO>> lessonsBySection = buildLessonsBySectionId(
                sections.stream().map(CourseSection::getId).toList()
        );

        Map<Long, List<CourseSectionDTO>> sectionsByChapter = new LinkedHashMap<>();
        for (CourseSection section : sections) {
            List<CourseLessonDTO> lessons = lessonsBySection.getOrDefault(section.getId(), List.of());
            sectionsByChapter.computeIfAbsent(section.getChapter().getId(), ignored -> new ArrayList<>())
                    .add(toSectionDTO(section, lessons));
        }
        return sectionsByChapter;
    }

    private Map<Long, List<CourseLessonDTO>> buildLessonsBySectionId(List<Long> sectionIds) {
        if (sectionIds.isEmpty()) {
            return Map.of();
        }

        List<CourseLesson> lessons = lessonRepository.findBySectionIdInOrderBySectionIdAscLessonOrderAscIdAsc(
                sectionIds
        );
        if (lessons.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<CourseLessonDTO>> lessonsBySection = new LinkedHashMap<>();
        for (CourseLesson lesson : lessons) {
            lessonsBySection.computeIfAbsent(lesson.getSection().getId(), ignored -> new ArrayList<>())
                    .add(toLessonDTO(lesson));
        }
        return lessonsBySection;
    }

    private CourseDTO toCourseDTO(Course course, List<CourseChapterDTO> chapters) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .chapters(new ArrayList<>(chapters))
                .build();
    }

    private CourseChapterDTO toChapterDTO(CourseChapter chapter, List<CourseSectionDTO> sections) {
        return CourseChapterDTO.builder()
                .id(chapter.getId())
                .courseId(chapter.getCourse().getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .chapterOrder(chapter.getChapterOrder())
                .sections(new ArrayList<>(sections))
                .build();
    }

    private CourseSectionDTO toSectionDTO(CourseSection section, List<CourseLessonDTO> lessons) {
        return CourseSectionDTO.builder()
                .id(section.getId())
                .chapterId(section.getChapter().getId())
                .type(section.getType())
                .title(section.getTitle())
                .level(section.getLevel())
                .topic(section.getTopic())
                .status(section.getStatus())
                .sectionOrder(section.getSectionOrder())
                .lessons(new ArrayList<>(lessons))
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

    private String requiredText(String value) {
        return value.trim();
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
