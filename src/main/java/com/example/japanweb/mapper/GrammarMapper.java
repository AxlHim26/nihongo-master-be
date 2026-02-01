package com.example.japanweb.mapper;

import com.example.japanweb.dto.GrammarBookDTO;
import com.example.japanweb.dto.GrammarChapterDetailDTO;
import com.example.japanweb.dto.GrammarExampleDTO;
import com.example.japanweb.dto.GrammarPointDTO;
import com.example.japanweb.entity.GrammarBook;
import com.example.japanweb.entity.GrammarChapter;
import com.example.japanweb.entity.GrammarExample;
import com.example.japanweb.entity.GrammarPoint;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrammarMapper {

    GrammarBookDTO toBookDTO(GrammarBook book);

    GrammarChapterDetailDTO toChapterDetailDTO(GrammarChapter chapter);

    GrammarPointDTO toPointDTO(GrammarPoint point);

    GrammarExampleDTO toExampleDTO(GrammarExample example);
}
