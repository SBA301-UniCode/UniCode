package com.example.unicode.mapper;

import com.example.unicode.dto.request.PracticeExamRequest;
import com.example.unicode.dto.request.TestCaseRequest;
import com.example.unicode.dto.response.PracticeExamResponse;
import com.example.unicode.dto.response.TestCaseResponse;
import com.example.unicode.entity.PracticeExam;
import com.example.unicode.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PracticeExamMapper {

    @Mapping(target = "practiceId", ignore = true)
    @Mapping(target = "testCaseList", source = "testCases")
    @Mapping(target = "totalTestCase", expression = "java(request.getTestCases().size())")
    PracticeExam toEntity(PracticeExamRequest request);

    @Mapping(target = "testCases", source = "testCaseList")
    PracticeExamResponse toResponse(PracticeExam exam);

    @Mapping(target = "testcaseId", ignore = true)
    @Mapping(target = "practiceExam", ignore = true)
    TestCase toEntity(TestCaseRequest request);

    TestCaseResponse toResponse(TestCase testCase);
}
