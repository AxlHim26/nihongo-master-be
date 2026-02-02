package com.example.japanweb.dto.response.vocab;

import com.example.japanweb.entity.VocabCourse;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class VocabCourseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String level;
    private VocabCourse.CourseType type;
    private Integer totalWords;
    private LocalDateTime createdAt;
}
