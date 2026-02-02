package com.example.japanweb.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResultDTO {
    private boolean isCorrect;
    private int currentHP;
    private int score;
    private String message;
    private String correctAnswer; // Only shown when wrong
    private boolean isGameOver;
    private boolean isComplete;
    private int questionsRemaining;
}
