package com.example.japanweb.service;

import com.example.japanweb.dto.response.game.GameResultDTO;
import com.example.japanweb.dto.response.game.GameStartResponseDTO;

public interface GameService {
    /**
     * Start a new game session for a course.
     */
    GameStartResponseDTO startGame(Long courseId, Long userId);

    /**
     * Submit an answer for a question in the game.
     */
    GameResultDTO submitAnswer(String sessionId, Long questionId, String answer, Long userId);
}
