package com.example.japanweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for vocabulary entries in responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabEntryDTO {
    private Long id;
    private String term;
    private String reading;
    private String meaning;
    private String example;
    private String level;
}
