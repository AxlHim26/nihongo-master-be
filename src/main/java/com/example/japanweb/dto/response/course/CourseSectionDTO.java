package com.example.japanweb.dto.response.course;

import com.example.japanweb.entity.CourseSectionStatus;
import com.example.japanweb.entity.CourseSectionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseSectionDTO {
    private Long id;
    private Long chapterId;
    private CourseSectionType type;
    private String title;
    private String level;
    private String topic;
    private CourseSectionStatus status;
    private Integer sectionOrder;
    private List<CourseLessonDTO> lessons;
}
