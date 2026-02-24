package com.example.unicode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptSubmitRequest {
    private UUID examAttemptId;
    private List<AnswerSubmitRequest> answers;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerSubmitRequest {
        private UUID questionBankId;
        private UUID selectedOptionId ;
    }
}
