package com.example.unicode.dto.response;

import com.example.unicode.entity.Lesson;
import com.example.unicode.enums.QuestionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankResponse {
    private UUID questionBankId;
    private String questionText;
    private String imageUrl;
    private int numberAnswers;
    private QuestionType questionType;
    private UUID lessonId;
    private List<QuestionOptionResponse> options;

}
