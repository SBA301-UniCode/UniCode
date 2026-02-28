package com.example.unicode.mapper;

import com.example.unicode.dto.request.QuestionOptionRequest;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.entity.QuestionOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionOptionMapper {
    QuestionOption toEntity(QuestionOptionRequest request);
    QuestionBankResponse toResponse(QuestionOption questionOption);
}
