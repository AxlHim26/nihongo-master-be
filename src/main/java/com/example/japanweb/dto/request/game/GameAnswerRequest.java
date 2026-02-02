package com.example.japanweb.dto.request.game;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting a game answer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameAnswerRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer is required")
    private String answer;
}
