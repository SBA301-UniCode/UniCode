package com.example.unicode.dto.request;

import com.example.unicode.entity.PracticeExam;
import com.example.unicode.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeExamRequest {
    private String title;
    private String description;
    private PracticeExam.CodeLanguage language;
    private PracticeExam.Difficulty difficulty;
    private String starterCode;
    private String rightCode;
    private List<TestCaseRequest> testCases;
    private String inputType;
    private String returnType;

}


