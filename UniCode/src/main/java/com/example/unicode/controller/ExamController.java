package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.response.ExamResponse;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/{contentId}")
    @Operation(summary = "Create exam from lesson")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@PathVariable UUID contentId,@RequestBody ExamRequest request) {
            return ResponseEntity.ok(ApiResponse.success(examService.createExam(contentId,request)));
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




}
