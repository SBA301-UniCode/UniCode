package com.example.unicode.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionRequest {
    private UUID optionId;
    private String answerText;
    @JsonProperty("isCorrect")
    private boolean isCorrect;
}
