package com.example.japanweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for vocabulary course details including entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabCourseDetailDTO {
    private Long id;
    private String title;
    private String level;
    private String type;
    private Integer totalWords;
    private List<VocabEntryDTO> entries;
}
