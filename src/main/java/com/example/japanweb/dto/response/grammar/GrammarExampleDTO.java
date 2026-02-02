package com.example.japanweb.dto.response.grammar;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrammarExampleDTO {
    private Long id;
    private String sentenceJp;
    private String sentenceVn;
    private String audioUrl;
}
