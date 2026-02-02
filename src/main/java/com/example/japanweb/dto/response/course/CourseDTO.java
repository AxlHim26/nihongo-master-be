package com.example.japanweb.dto.response.course;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CourseDTO {
    private Long id;
    private String thumbnailUrl;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CourseChapterDTO> chapters;
}
