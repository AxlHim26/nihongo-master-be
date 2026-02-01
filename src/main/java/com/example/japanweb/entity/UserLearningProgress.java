package com.example.japanweb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tracks user learning progress for vocabulary entries using SRS (Spaced
 * Repetition System).
 * Implements SM-2 algorithm parameters.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_learning_progress", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id",
        "vocab_id" }), indexes = {
                @Index(name = "idx_ulp_user", columnList = "user_id"),
                @Index(name = "idx_ulp_next_review", columnList = "user_id, next_review_at")
        })
public class UserLearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private VocabEntry vocab;

    @Column(name = "next_review_at", nullable = false)
    @Builder.Default
    private LocalDateTime nextReviewAt = LocalDateTime.now();

    /**
     * Number of days until next review (SM-2 interval)
     */
    @Column(name = "interval_days", nullable = false)
    @Builder.Default
    private Integer intervalDays = 0;

    /**
     * SM-2 Ease Factor (EF), default 2.5
     * Minimum value is 1.3
     */
    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.50");

    /**
     * Number of consecutive correct reviews
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer repetitions = 0;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
