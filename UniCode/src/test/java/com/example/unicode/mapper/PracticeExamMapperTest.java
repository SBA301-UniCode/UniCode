package com.example.unicode.mapper;

import com.example.unicode.dto.request.PracticeExamRequest;
import com.example.unicode.dto.request.TestCaseRequest;
import com.example.unicode.dto.response.TestCaseResponse;
import com.example.unicode.entity.PracticeExam;
import com.example.unicode.entity.TestCase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PracticeExamMapperTest {

    private final PracticeExamMapper mapper = Mappers.getMapper(PracticeExamMapper.class);

    @Test
    void toEntityAndToResponseShouldMapTestCasesAndCount() {
        TestCaseRequest tc1 = new TestCaseRequest("1", "2", TestCase.OutputType.NUMBER, false, "d1");
        TestCaseRequest tc2 = new TestCaseRequest("3", "4", TestCase.OutputType.STRING, true, "d2");
        PracticeExamRequest request = new PracticeExamRequest(
                "P1", "desc", PracticeExam.CodeLanguage.JAVA, PracticeExam.Difficulty.EASY,
                "starter", "right", List.of(tc1, tc2), "[]", "int"
        );

        PracticeExam entity = mapper.toEntity(request);
        assertEquals("P1", entity.getTitle());
        assertEquals(2, entity.getTotalTestCase());
        assertEquals(2, entity.getTestCaseList().size());

        var response = mapper.toResponse(entity);
        assertEquals(2, response.getTestCases().size());
    }

    @Test
    void testCaseMappingShouldWorkBothDirections() {
        TestCaseRequest request = new TestCaseRequest("in", "out", TestCase.OutputType.ARRAY, false, "d");
        TestCase entity = mapper.toEntity(request);
        assertEquals("in", entity.getInputData());
        assertEquals(TestCase.OutputType.ARRAY, entity.getOutputType());

        TestCaseResponse response = mapper.toResponse(entity);
        assertEquals("out", response.getExpectedOutput());
    }
}
