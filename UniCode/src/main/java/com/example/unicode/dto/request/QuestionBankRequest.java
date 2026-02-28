package com.example.unicode.dto.request;

import com.example.unicode.entity.Lesson;
import com.example.unicode.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankRequest {
    private String questionText;
    private String imageUrl;
    private int numberAnswers;
    private QuestionType questionType;
    private List<QuestionOptionRequest> options = new ArrayList<>();;

}
