package com.example.japanweb.dto.response.course;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseChapterDTO {
    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer chapterOrder;
    private List<CourseSectionDTO> sections;
}
