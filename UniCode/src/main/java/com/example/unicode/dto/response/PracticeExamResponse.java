package com.example.unicode.dto.response;

import com.example.unicode.entity.PracticeExam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeExamResponse {
    private UUID practiceId;
    private String title;
    private String description;
    private PracticeExam.CodeLanguage language;
    private PracticeExam.Difficulty difficulty;
    private String starterCode;
    private String rightCode;
    private int totalTestCase;
    private List<TestCaseResponse> testCases;
}