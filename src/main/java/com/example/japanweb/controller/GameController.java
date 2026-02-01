package com.example.japanweb.controller;

import com.example.japanweb.dto.*;
import com.example.japanweb.entity.User;
import com.example.japanweb.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Speed Review Game.
 */
@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * Start a new game session.
     */
    @PostMapping("/start")
    public ApiResponse<GameStartResponseDTO> startGame(
            @AuthenticationPrincipal User user,
            @RequestBody GameStartRequest request) {

        GameStartResponseDTO response = gameService.startGame(request.getCourseId(), user.getId());
        return ApiResponse.success(response, "Game started successfully!");
    }

    /**
     * Submit an answer for the current question.
     */
    @PostMapping("/answer")
    public ApiResponse<GameResultDTO> submitAnswer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GameAnswerRequest request) {

        GameResultDTO result = gameService.submitAnswer(
                request.getSessionId(),
                request.getQuestionId(),
                request.getAnswer(),
                user.getId());

        return ApiResponse.success(result);
    }
}
