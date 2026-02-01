package com.example.japanweb.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GrammarChapterDetailDTO {
    private Long id;
    private String title;
    private Integer chapterOrder;
    private List<GrammarPointDTO> grammarPoints;
}
