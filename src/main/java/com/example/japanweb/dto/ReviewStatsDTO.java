package com.example.japanweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user review statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDTO {
    private long totalLearning;
    private long dueForReview;
}
