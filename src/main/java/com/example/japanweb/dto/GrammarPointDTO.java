package com.example.japanweb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GrammarPointDTO {
    private Long id;
    private String title;
    private String meaning;
    private String structure;
    private String note;
    private List<GrammarExampleDTO> examples;
}
