package com.example.unicode.service;

import com.example.unicode.dto.request.ExamAttemptSubmitRequest;
import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.request.PracticeExamRequest;
import com.example.unicode.dto.request.PracticeSubmitRequest;
import com.example.unicode.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    ExamResponse createExam(UUID lessonId, ExamRequest request);
    ExamResponse updateExam(UUID examId, ExamRequest request);
    void changeStatus(UUID examId);
    List<QuestionBankResponse> getQuestionsByExam(UUID examId);
    ExamResponse getExamById(UUID examId);
    ExamAttemptRespone startExam(UUID examId);
    ExamAttempResultsResponse submitExam(ExamAttemptSubmitRequest request);
    List<AnswerHistoryResponse> getExamAttemptHistory(UUID examAttemptId);
    ExamAttempResultsResponse getExamAttemptResults(UUID examAttemptId);
    PracticeExamResponse createPracticeExam(UUID lessonId, PracticeExamRequest request);
    PracticeExamResponse getPracticeExamById(UUID id);
    void deletePracticeExam(UUID id);
    PracticeExamResponse updatePracticeExam(UUID id, PracticeExamRequest request);
    PracticeStartResponse startPracticeExam(UUID contentId);
    PracticeResultResponse submitPracticeExam(PracticeSubmitRequest request);
    List<ExamAttempResultsResponse> getMyExamAttempt(int page ,int size , UUID examId);
}
