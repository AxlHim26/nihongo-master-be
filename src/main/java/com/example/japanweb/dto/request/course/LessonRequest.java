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
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    @Size(max = 200, message = "Lesson title must not exceed 200 characters")
    private String title;

    @Size(max = 500, message = "Video URL must not exceed 500 characters")
    private String videoUrl;

    @Size(max = 500, message = "PDF URL must not exceed 500 characters")
    private String pdfUrl;

    @Min(value = 0, message = "Lesson order must be >= 0")
    @Builder.Default
    private Integer lessonOrder = 0;
}
