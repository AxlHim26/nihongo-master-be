package com.example.japanweb.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStartResponseDTO {
    private String sessionId;
    private List<GameQuestionDTO> questions;
    private int totalQuestions;
    private int initialHP;
}
