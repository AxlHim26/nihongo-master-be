package com.example.japanweb.mapper;

import com.example.japanweb.dto.VocabCourseDTO;
import com.example.japanweb.entity.VocabCourse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VocabMapper {
    VocabCourseDTO toCourseDTO(VocabCourse course);
}
