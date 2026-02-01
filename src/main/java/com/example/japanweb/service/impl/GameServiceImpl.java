package com.example.japanweb.service.impl;

import com.example.japanweb.dto.GameQuestionDTO;
import com.example.japanweb.dto.GameResultDTO;
import com.example.japanweb.dto.GameStartResponseDTO;
import com.example.japanweb.entity.VocabEntry;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.redis.GameSession;
import com.example.japanweb.repository.VocabEntryRepository;
import com.example.japanweb.service.GameService;
import com.example.japanweb.service.SrsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Speed Review Game Service implementation with Redis session management
 * and SRS integration for correct answers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final VocabEntryRepository vocabEntryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SrsService srsService;

    private static final int INITIAL_HP = 3;
    private static final int QUESTION_COUNT = 20;
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);
    private static final String GAME_KEY_PREFIX = "game:";

    @Override
    public GameStartResponseDTO startGame(Long courseId, Long userId) {
        // 1. Fetch random words from the course
        List<VocabEntry> randomEntries = vocabEntryRepository
                .findRandomEntriesByCourseId(courseId, QUESTION_COUNT);

        if (randomEntries.isEmpty()) {
            throw new ApiException(ErrorCode.GAME_INSUFFICIENT_WORDS,
                    "Not enough words in course to start a game (need at least 1)");
        }

        // 2. Generate unique Session ID
        String sessionId = UUID.randomUUID().toString();

        // 3. Prepare GameSession with answer map
        Map<Long, String> answers = new HashMap<>();
        for (VocabEntry entry : randomEntries) {
            answers.put(entry.getId(), entry.getMeaning());
        }

        GameSession session = GameSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .courseId(courseId)
                .correctAnswers(answers)
                .currentHP(INITIAL_HP)
                .score(0)
                .questionsAnswered(0)
                .startTime(System.currentTimeMillis())
                .build();

        // 4. Save to Redis with TTL
        redisTemplate.opsForValue().set(GAME_KEY_PREFIX + sessionId, session, SESSION_TTL);

        log.info("Started game session {} for user {} on course {} with {} questions",
                sessionId, userId, courseId, randomEntries.size());

        // 5. Build question list (without answers)
        List<GameQuestionDTO> questions = randomEntries.stream()
                .map(entry -> GameQuestionDTO.builder()
                        .id(entry.getId())
                        .term(entry.getTerm())
                        .reading(entry.getReading())
                        .options("Type the meaning")
                        .build())
                .collect(Collectors.toList());

        return GameStartResponseDTO.builder()
                .sessionId(sessionId)
                .questions(questions)
                .totalQuestions(questions.size())
                .initialHP(INITIAL_HP)
                .build();
    }

    @Override
    public GameResultDTO submitAnswer(String sessionId, Long questionId, String answer, Long userId) {
        String key = GAME_KEY_PREFIX + sessionId;
        GameSession session = (GameSession) redisTemplate.opsForValue().get(key);

        if (session == null) {
            throw new ApiException(ErrorCode.GAME_SESSION_NOT_FOUND);
        }

        // Verify user owns this session
        if (!session.getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.AUTH_ACCESS_DENIED,
                    "This game session belongs to another user");
        }

        String correctAnswer = session.getCorrectAnswers().get(questionId);
        if (correctAnswer == null) {
            throw new ApiException(ErrorCode.GAME_INVALID_QUESTION);
        }

        boolean isCorrect = correctAnswer.equalsIgnoreCase(answer.trim());
        int currentHP = session.getCurrentHP();
        int score = session.getScore();
        int questionsAnswered = session.getQuestionsAnswered() + 1;
        String message;
        String correctAnswerDisplay = null;

        if (isCorrect) {
            score += 10; // Base score for correct answer
            message = "✓ Correct!";

            // Update SRS progress for the user
            try {
                srsService.getOrCreateProgress(userId, questionId);
                var progress = srsService.getOrCreateProgress(userId, questionId);
                srsService.processReview(progress, SrsService.Rating.GOOD);
                log.debug("Updated SRS progress for user {} vocab {}", userId, questionId);
            } catch (Exception e) {
                log.warn("Failed to update SRS for vocab {}: {}", questionId, e.getMessage());
                // Don't fail the game if SRS update fails
            }
        } else {
            currentHP--;
            message = "✗ Wrong!";
            correctAnswerDisplay = correctAnswer;

            // Mark as failed review in SRS
            try {
                var progress = srsService.getOrCreateProgress(userId, questionId);
                srsService.processReview(progress, SrsService.Rating.AGAIN);
            } catch (Exception e) {
                log.warn("Failed to update SRS for vocab {}: {}", questionId, e.getMessage());
            }
        }

        session.setCurrentHP(currentHP);
        session.setScore(score);
        session.setQuestionsAnswered(questionsAnswered);

        boolean isGameOver = currentHP <= 0;
        boolean isComplete = questionsAnswered >= session.getCorrectAnswers().size();

        if (isGameOver) {
            redisTemplate.delete(key);
            message = "💀 Game Over! You ran out of HP.";
            log.info("Game {} ended: Game Over. Final score: {}", sessionId, score);
        } else if (isComplete) {
            redisTemplate.delete(key);
            message = "🎉 Congratulations! You completed the review!";
            log.info("Game {} ended: Completed. Final score: {} with {} HP remaining",
                    sessionId, score, currentHP);
        } else {
            // Refresh session TTL
            redisTemplate.opsForValue().set(key, session, SESSION_TTL);
        }

        return GameResultDTO.builder()
                .isCorrect(isCorrect)
                .currentHP(currentHP)
                .score(score)
                .message(message)
                .correctAnswer(correctAnswerDisplay)
                .isGameOver(isGameOver)
                .isComplete(isComplete)
                .questionsRemaining(session.getCorrectAnswers().size() - questionsAnswered)
                .build();
    }
}
