package com.example.unicode.mapper;

import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.response.ExamResponse;
import com.example.unicode.entity.Exam;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface ExamMapper {
    Exam toEntity(ExamRequest request);
    ExamResponse toResponse(Exam exam);
}
