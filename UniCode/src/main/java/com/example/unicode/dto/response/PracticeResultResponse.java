package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeResultResponse {
    private UUID submissionId;
    private UUID practiceId;
    private int passed;
    private int failed;
    private List<TestCaseResultResponse> results;
}