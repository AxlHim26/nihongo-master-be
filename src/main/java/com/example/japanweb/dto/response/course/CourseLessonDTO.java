package com.example.japanweb.dto.response.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseLessonDTO {
    private Long id;
    private Long sectionId;
    private String title;
    private String videoUrl;
    private String pdfUrl;
    private Integer lessonOrder;
}
