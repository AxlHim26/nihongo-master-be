package com.example.japanweb.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Game session stored in Redis for Speed Review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private Long userId;
    private Long courseId;
    private Map<Long, String> correctAnswers; // VocabId -> Correct Answer (Meaning)
    private int currentHP;
    private int score;
    private int questionsAnswered;
    private long startTime;
}
