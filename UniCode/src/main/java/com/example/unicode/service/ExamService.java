package com.example.unicode.service;

import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.response.ExamResponse;
import com.example.unicode.dto.response.QuestionBankResponse;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    ExamResponse createExam(UUID contentId, ExamRequest request);
    ExamResponse updateExam(UUID examId, ExamRequest request);
    void changeStatus(UUID examId);
    List<QuestionBankResponse> getQuestionsByExam(UUID examId);
    ExamResponse getExamById(UUID examId);
}
