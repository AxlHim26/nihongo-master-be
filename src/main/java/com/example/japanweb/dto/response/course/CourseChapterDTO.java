package com.example.japanweb.dto.response.course;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CourseChapterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer chapterOrder;
    private List<CourseSectionDTO> sections;
}
