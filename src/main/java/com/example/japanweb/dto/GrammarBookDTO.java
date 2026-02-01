package com.example.japanweb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrammarBookDTO {
    private Long id;
    private String title;
    private String level;
    private Boolean isActive;
}
