package com.example.unicode.dto.response;

import com.example.unicode.entity.QuestionBank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptRespone {
    private String examAttemptId;
    private String examName;
    private String learnerName;
    private List<QuestionBankResponse> questions = new ArrayList<>();
}
