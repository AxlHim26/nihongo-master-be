package com.example.japanweb.dto.response.course;

import com.example.japanweb.entity.CourseSectionType;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CourseSectionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long chapterId;
    private CourseSectionType type;
    private String title;
    private Integer sectionOrder;
    private List<CourseLessonDTO> lessons;
}
