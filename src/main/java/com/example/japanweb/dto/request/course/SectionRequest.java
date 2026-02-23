package com.example.japanweb.dto.request.course;

import com.example.japanweb.entity.CourseSectionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @Min(value = 0, message = "Section order must be >= 0")
    @Builder.Default
    private Integer sectionOrder = 0;
}
