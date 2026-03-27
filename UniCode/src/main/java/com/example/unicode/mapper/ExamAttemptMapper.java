package com.example.unicode.mapper;

import com.example.unicode.dto.response.ExamAttempResultsResponse;
import com.example.unicode.dto.response.ExamAttemptRespone;
import com.example.unicode.entity.ExamAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamAttemptMapper {
    @Mapping(target = "examName", source = "exam.name")
    @Mapping(target = "learnerName", source = "learner.name")
    ExamAttemptRespone toResponse(ExamAttempt examAttempt);

    @Mapping(target = "examName", source = "exam.name")
    @Mapping(target = "learnerName", source = "learner.name")
    @Mapping(target = "examId",source = "exam.examId")
    ExamAttempResultsResponse toResultsResponse(ExamAttempt examAttempt);
}
