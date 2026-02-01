package com.example.japanweb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting SRS review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Vocab ID is required")
    private Long vocabId;

    /**
     * Rating: 0 = Again, 1 = Hard, 2 = Good, 3 = Easy
     */
    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be between 0 and 3")
    @Max(value = 3, message = "Rating must be between 0 and 3")
    private Integer rating;
}
