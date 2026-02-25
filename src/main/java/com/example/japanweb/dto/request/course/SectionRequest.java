package com.example.japanweb.dto.request.course;

import com.example.japanweb.entity.CourseSectionType;
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
}
