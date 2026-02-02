package com.example.japanweb.dto.response.course;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class CourseLessonDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectionId;
    private String title;
    private String videoUrl;
    private String pdfUrl;
    private Integer lessonOrder;
}
