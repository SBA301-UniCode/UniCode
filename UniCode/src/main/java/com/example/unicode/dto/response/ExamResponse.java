package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private UUID examId;
    private String name;
    private int duration;
    private double passScore;
    private int numberQuestions;
}
