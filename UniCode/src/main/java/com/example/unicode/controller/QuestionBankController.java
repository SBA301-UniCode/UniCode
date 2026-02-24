package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.QuestionBankRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.service.QuestionBankSerivce;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/question-banks")
@RequiredArgsConstructor
@Tag(name = "Question bank", description = "Question bank management APIs")
public class QuestionBankController {
    private final QuestionBankSerivce questionBankSerivce;

    @PostMapping("/{lessonId}")
    @Operation(summary = "Create question for a lesson with options")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> createQuestion(@PathVariable UUID lessonId,@RequestBody QuestionBankRequest request) {
        return ResponseEntity.ok(ApiResponse.success(questionBankSerivce.createQuestion(lessonId, request)));
    }

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get all questions by lesson ID with pagination")
    public ResponseEntity<ApiResponse<PageResponse<QuestionBankResponse>>> getAllQuestionsByLesson(@PathVariable UUID lessonId, int page, int size) {
        return ResponseEntity.ok(ApiResponse.success(questionBankSerivce.getAllQuestionsByLesson(lessonId, page, size)));
    }

    @GetMapping("/detail/{questionBankId}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> getQuestionById(@PathVariable UUID questionBankId) {
        return ResponseEntity.ok(ApiResponse.success(questionBankSerivce.getQuestionById(questionBankId)));
    }

    @PutMapping("/{questionBankId}")
    @Operation(summary = "Update question by ID")
    public ResponseEntity<ApiResponse<QuestionBankResponse>> updateQuestion(@PathVariable UUID questionBankId,@RequestBody QuestionBankRequest request) {
        return ResponseEntity.ok(ApiResponse.success(questionBankSerivce.updateQuestion(questionBankId, request)));
    }
    @DeleteMapping("/{questionBankId}")
    @Operation(summary = "Delete question by ID")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable UUID questionBankId) {
        questionBankSerivce.deleteQuestion(questionBankId);
        return ResponseEntity.ok(ApiResponse.success("Delete question successfully"));
    }

}
