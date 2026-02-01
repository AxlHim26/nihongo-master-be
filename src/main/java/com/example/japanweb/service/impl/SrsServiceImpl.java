package com.example.japanweb.service.impl;

import com.example.japanweb.entity.User;
import com.example.japanweb.entity.UserLearningProgress;
import com.example.japanweb.entity.VocabEntry;
import com.example.japanweb.exception.ApiException;
import com.example.japanweb.exception.ErrorCode;
import com.example.japanweb.repository.UserLearningProgressRepository;
import com.example.japanweb.repository.UserRepository;
import com.example.japanweb.repository.VocabEntryRepository;
import com.example.japanweb.service.SrsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * SM-2 (SuperMemo 2) Algorithm Implementation.
 * 
 * The algorithm works as follows:
 * 1. Initial interval: 1 day after first review, 6 days after second
 * 2. Subsequent intervals: previous interval * ease factor
 * 3. Ease factor adjusts based on answer quality (min 1.3)
 * 
 * Formula for ease factor adjustment:
 * EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
 * where q = quality of response (0-5 scale)
 * 
 * We map our 0-3 rating to 0-5:
 * AGAIN(0) -> 0, HARD(1) -> 2, GOOD(2) -> 3, EASY(3) -> 5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SrsServiceImpl implements SrsService {

    private final UserLearningProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final VocabEntryRepository vocabEntryRepository;

    private static final BigDecimal MIN_EASE_FACTOR = new BigDecimal("1.30");
    private static final BigDecimal DEFAULT_EASE_FACTOR = new BigDecimal("2.50");

    @Override
    @Transactional
    public UserLearningProgress processReview(UserLearningProgress progress, Rating rating) {
        // Map our 0-3 rating to SM-2's 0-5 scale
        int quality = mapRatingToQuality(rating);

        LocalDateTime now = LocalDateTime.now();
        progress.setLastReviewedAt(now);

        if (quality < 3) {
            // Failed recall - reset repetitions but keep ease factor
            progress.setRepetitions(0);
            progress.setIntervalDays(1);
            progress.setNextReviewAt(now.plusDays(1));

            log.debug("Review failed for vocab {}: reset to 1 day interval",
                    progress.getVocab().getId());
        } else {
            // Successful recall
            int repetitions = progress.getRepetitions() + 1;
            progress.setRepetitions(repetitions);

            // Calculate new interval
            int newInterval = calculateInterval(repetitions, progress.getIntervalDays(),
                    progress.getEaseFactor());
            progress.setIntervalDays(newInterval);
            progress.setNextReviewAt(now.plusDays(newInterval));

            // Adjust ease factor
            BigDecimal newEaseFactor = adjustEaseFactor(progress.getEaseFactor(), quality);
            progress.setEaseFactor(newEaseFactor);

            log.debug("Review success for vocab {}: rep={}, interval={} days, EF={}",
                    progress.getVocab().getId(), repetitions, newInterval, newEaseFactor);
        }

        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public UserLearningProgress initializeProgress(Long userId, Long vocabId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTH_USER_NOT_FOUND));

        VocabEntry vocab = vocabEntryRepository.findById(vocabId)
                .orElseThrow(() -> new ApiException(ErrorCode.VOCAB_ENTRY_NOT_FOUND));

        UserLearningProgress progress = UserLearningProgress.builder()
                .user(user)
                .vocab(vocab)
                .nextReviewAt(LocalDateTime.now())
                .intervalDays(0)
                .easeFactor(DEFAULT_EASE_FACTOR)
                .repetitions(0)
                .build();

        return progressRepository.save(progress);
    }

    @Override
    @Transactional
    public UserLearningProgress getOrCreateProgress(Long userId, Long vocabId) {
        return progressRepository.findByUserIdAndVocabId(userId, vocabId)
                .orElseGet(() -> initializeProgress(userId, vocabId));
    }

    /**
     * Map our simple 0-3 rating to SM-2's 0-5 quality scale.
     */
    private int mapRatingToQuality(Rating rating) {
        return switch (rating) {
            case AGAIN -> 0; // Complete failure
            case HARD -> 2; // Correct with serious difficulty
            case GOOD -> 3; // Correct with some difficulty
            case EASY -> 5; // Perfect response
        };
    }

    /**
     * Calculate the new interval based on repetitions and ease factor.
     * 
     * SM-2 interval formula:
     * - n = 1: interval = 1 day
     * - n = 2: interval = 6 days
     * - n > 2: interval = previous interval * EF
     */
    private int calculateInterval(int repetitions, int previousInterval, BigDecimal easeFactor) {
        if (repetitions == 1) {
            return 1;
        } else if (repetitions == 2) {
            return 6;
        } else {
            // For n > 2: I(n) = I(n-1) * EF
            BigDecimal interval = BigDecimal.valueOf(previousInterval)
                    .multiply(easeFactor)
                    .setScale(0, RoundingMode.CEILING);
            return Math.max(1, interval.intValue());
        }
    }

    /**
     * Adjust ease factor based on quality of response.
     * 
     * SM-2 formula: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
     */
    private BigDecimal adjustEaseFactor(BigDecimal currentEF, int quality) {
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        int diff = 5 - quality;
        double adjustment = 0.1 - diff * (0.08 + diff * 0.02);

        BigDecimal newEF = currentEF.add(BigDecimal.valueOf(adjustment))
                .setScale(2, RoundingMode.HALF_UP);

        // Minimum EF is 1.3
        if (newEF.compareTo(MIN_EASE_FACTOR) < 0) {
            return MIN_EASE_FACTOR;
        }

        return newEF;
    }
}
