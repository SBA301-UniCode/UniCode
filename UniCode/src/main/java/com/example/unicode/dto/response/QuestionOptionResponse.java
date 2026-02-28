package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionResponse {
    private UUID optionId;
    private String answerText;
    private boolean isCorrect;
}
