package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerHistoryResponse {
    private String questionText;
    private String selectedAnswer;
    private boolean isCorrect;
    private String rightAnswer;

}
