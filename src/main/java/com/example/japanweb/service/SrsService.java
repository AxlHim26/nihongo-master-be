package com.example.japanweb.service;

import com.example.japanweb.entity.UserLearningProgress;

/**
 * Service for Spaced Repetition System (SM-2 Algorithm).
 * Calculates next review dates based on user performance.
 */
public interface SrsService {

    /**
     * SRS Rating enum representing user's answer quality.
     */
    enum Rating {
        AGAIN(0), // Complete blackout, wrong answer
        HARD(1), // Correct but with difficulty
        GOOD(2), // Correct with some hesitation
        EASY(3); // Perfect, instant recall

        private final int value;

        Rating(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Rating fromValue(int value) {
            for (Rating rating : values()) {
                if (rating.value == value) {
                    return rating;
                }
            }
            throw new IllegalArgumentException("Invalid rating value: " + value);
        }
    }

    /**
     * Process a review and update the learning progress.
     * 
     * @param progress The current learning progress
     * @param rating   The user's rating of their recall
     * @return Updated learning progress with new interval and next review date
     */
    UserLearningProgress processReview(UserLearningProgress progress, Rating rating);

    /**
     * Initialize progress for a new vocabulary entry.
     * 
     * @param userId  The user's ID
     * @param vocabId The vocabulary entry's ID
     * @return New learning progress entry
     */
    UserLearningProgress initializeProgress(Long userId, Long vocabId);

    /**
     * Get or create progress for a vocabulary entry.
     */
    UserLearningProgress getOrCreateProgress(Long userId, Long vocabId);
}
