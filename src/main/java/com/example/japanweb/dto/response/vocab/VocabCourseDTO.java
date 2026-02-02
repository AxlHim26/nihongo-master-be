package com.example.japanweb.dto.response.vocab;

import com.example.japanweb.entity.VocabCourse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VocabCourseDTO {
    private Long id;
    private String title;
    private String level;
    private VocabCourse.CourseType type;
    private Integer totalWords;
    private LocalDateTime createdAt;
}
