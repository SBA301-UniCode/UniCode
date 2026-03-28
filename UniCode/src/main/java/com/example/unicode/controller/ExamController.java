package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ExamAttemptSubmitRequest;
import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.request.PracticeExamRequest;
import com.example.unicode.dto.request.PracticeSubmitRequest;
import com.example.unicode.dto.response.*;
import com.example.unicode.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Tag(name = "Exam", description = "Exam management APIs")
public class ExamController {
    private final ExamService examService;

    @PostMapping("/quiz/{lessonId}")
    @Operation(summary = "Create exam quiz from lesson")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@PathVariable UUID lessonId,@RequestBody ExamRequest request) {
            return ResponseEntity.ok(ApiResponse.success(examService.createExam(lessonId,request)));
    }
    @PostMapping("/practice/{lessonId}")
    @Operation(summary = "Create practice exam from lesson")
    public ResponseEntity<ApiResponse<PracticeExamResponse>> createPracticeExam(@PathVariable UUID lessonId,@RequestBody PracticeExamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(examService.createPracticeExam(lessonId,request)));
    }
    @PatchMapping("/{examId}")
    @Operation(summary = "Change duration and pass score of exam ")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(@PathVariable UUID examId,@RequestBody ExamRequest request){
        return ResponseEntity.ok(ApiResponse.success(examService.updateExam(examId,request)));
    }
    @PatchMapping("/{examId}/status")
    @Operation(summary = "Detele exam or reactivate exam ")
    public ResponseEntity<ApiResponse<Void>> changeStatus(@PathVariable UUID examId){
        examService.changeStatus(examId);
        return ResponseEntity.ok(ApiResponse.success("Change status successfully"));
    }
    @GetMapping("/{examId}/questions")
    @Operation(summary = "Get all questions of an exam")
    public ResponseEntity<ApiResponse<List<QuestionBankResponse>>> getQuestionsByExam(@PathVariable UUID examId){
        return ResponseEntity.ok(ApiResponse.success(examService.getQuestionsByExam(examId)));
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam by ID")
    public ResponseEntity<ApiResponse<ExamResponse>> getExamById(@PathVariable UUID examId){
        return ResponseEntity.ok(ApiResponse.success(examService.getExamById(examId)));
    }
    @PostMapping("/{examId}/start")
    @Operation(summary = "Start an exam")
    public ResponseEntity<ApiResponse<ExamAttemptRespone>> startExam(@PathVariable UUID examId){
        return ResponseEntity.ok(ApiResponse.success(examService.startExam(examId)));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit an exam attempt")
    public ResponseEntity<ApiResponse<ExamAttempResultsResponse>> submitExam(@RequestBody ExamAttemptSubmitRequest request){
        return ResponseEntity.ok(ApiResponse.success(examService.submitExam(request)));
    }
    @GetMapping("/attempts/{examAttemptId}/history")
    @Operation(summary = "Get answer history of an exam attempt")
    public ResponseEntity<ApiResponse<List<AnswerHistoryResponse>>> getExamAttemptHistory(@PathVariable UUID examAttemptId){
        return ResponseEntity.ok(ApiResponse.success(examService.getExamAttemptHistory(examAttemptId)));
    }

    @GetMapping("/attempts/{examAttemptId}/results")
    @Operation(summary = "Get results of an exam attempt")
    public ResponseEntity<ApiResponse<ExamAttempResultsResponse>> getExamAttemptResults(@PathVariable UUID examAttemptId){
        return ResponseEntity.ok(ApiResponse.success(examService.getExamAttemptResults(examAttemptId)));
    }
    @GetMapping("/practice/{id}")
    @Operation(summary = "Get practice exam by ID")
    public ResponseEntity<ApiResponse<PracticeExamResponse>> getPracticeExamById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(examService.getPracticeExamById(id)));
    }

    @PutMapping("/practice/{id}")
    @Operation(summary = "Update practice exam")
    public ResponseEntity<ApiResponse<PracticeExamResponse>> updatePracticeExam(
            @PathVariable UUID id,
            @RequestBody PracticeExamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(examService.updatePracticeExam(id, request)));
    }

    @DeleteMapping("/practice/{id}")
    @Operation(summary = "Delete practice exam")
    public ResponseEntity<ApiResponse<Void>> deletePracticeExam(@PathVariable UUID id) {
        examService.deletePracticeExam(id);
        return ResponseEntity.ok(ApiResponse.success("Practice exam deleted successfully"));
    }
    @GetMapping("/practice/start/{contentId}")
    @Operation(summary = "Start practice exam")
    public ResponseEntity<ApiResponse<PracticeStartResponse>> startPracticeExam(@PathVariable UUID contentId) {
        return ResponseEntity.ok(ApiResponse.success(examService.startPracticeExam(contentId)));
    }
    @PostMapping("/practice-exams/submit")
    @Operation(summary = "Submit practice exam solution")
    public ResponseEntity<ApiResponse<PracticeResultResponse>> submitPracticeExam(
            @RequestBody PracticeSubmitRequest request) {
        return ResponseEntity.ok(ApiResponse.success(examService.submitPracticeExam(request)));
    }

    @GetMapping("/{examId}/my-attempt")
    public ResponseEntity<ApiResponse<List<ExamAttempResultsResponse>>> getMyAttempt(
            @PathVariable UUID examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(examService.getMyExamAttempt(page, size, examId)));
    }
}
