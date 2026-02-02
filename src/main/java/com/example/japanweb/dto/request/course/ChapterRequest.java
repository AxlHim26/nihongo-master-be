package com.example.japanweb.dto.request.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {

    @NotBlank(message = "Chapter title is required")
    @Size(max = 200, message = "Chapter title must not exceed 200 characters")
    private String title;

    private String description;

    @Min(value = 0, message = "Chapter order must be >= 0")
    @Builder.Default
    private Integer chapterOrder = 0;
}
