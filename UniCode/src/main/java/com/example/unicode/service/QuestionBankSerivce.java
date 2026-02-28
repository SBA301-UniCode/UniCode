package com.example.unicode.service;

import com.example.unicode.dto.request.QuestionBankRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.QuestionBankResponse;

import java.util.UUID;

public interface QuestionBankSerivce {
    QuestionBankResponse createQuestion(UUID lessonId, QuestionBankRequest request);

    PageResponse<QuestionBankResponse> getAllQuestionsByLesson(UUID lessonId,int page, int size);
    QuestionBankResponse getQuestionById(UUID questionBankId);

    QuestionBankResponse updateQuestion(UUID questionBankId, QuestionBankRequest request);
    void deleteQuestion(UUID questionBankId);
}
