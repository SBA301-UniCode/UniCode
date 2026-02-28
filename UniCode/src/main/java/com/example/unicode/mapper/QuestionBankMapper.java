package com.example.unicode.mapper;

import com.example.unicode.dto.request.QuestionBankRequest;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.entity.QuestionBank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionBankMapper {
    QuestionBank toEntity(QuestionBankRequest request);

    @Mapping(source = "lesson.lessonId", target = "lessonId")
    @Mapping(source = "questionOptionList", target = "options") // thêm dòng này
    QuestionBankResponse toResponse(QuestionBank questionBank);

    @Mapping(source = "lesson.lessonId", target = "lessonId")
    @Mapping(source = "questionOptionList", target = "options") // thêm dòng này
    List<QuestionBankResponse> toResponseList(List<QuestionBank> questionBanks);
}
