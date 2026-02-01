package com.example.japanweb.dto;

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
    private Long courseId;
}
