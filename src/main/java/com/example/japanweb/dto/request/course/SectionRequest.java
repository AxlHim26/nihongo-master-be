package com.example.japanweb.dto.request.course;

import com.example.japanweb.entity.CourseSectionStatus;
import com.example.japanweb.entity.CourseSectionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {

    @NotNull(message = "Section type is required")
    private CourseSectionType type;

    @NotBlank(message = "Section title is required")
    @Size(max = 200, message = "Section title must not exceed 200 characters")
    private String title;

    @Size(max = 50, message = "Level must not exceed 50 characters")
    private String level;

    @Size(max = 200, message = "Topic must not exceed 200 characters")
    private String topic;

    @Builder.Default
    private CourseSectionStatus status = CourseSectionStatus.ACTIVE;

    @Min(value = 0, message = "Section order must be >= 0")
    @Builder.Default
    private Integer sectionOrder = 0;
}
