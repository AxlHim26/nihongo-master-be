package com.example.japanweb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameQuestionDTO {
    private Long id;
    private String term;
    private String reading;
    private String options;
}
