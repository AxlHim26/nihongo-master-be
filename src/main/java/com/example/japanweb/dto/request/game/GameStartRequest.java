package com.example.japanweb.dto.request.game;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO to start a new game.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStartRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
