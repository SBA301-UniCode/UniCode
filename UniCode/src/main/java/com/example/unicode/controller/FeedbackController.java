package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.FeedbackRequest;
import com.example.unicode.dto.request.UpdateFeedbackRequest;
import com.example.unicode.dto.response.FeedBackResponse;
import com.example.unicode.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feedbacks")
@Tag(name = "Feedback", description = "Feedback management APIs")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FeedBackResponse>> create(
            @PathVariable UUID courseId, @RequestPart FeedbackRequest feedbackRequest,
            @RequestPart(required = false) List<MultipartFile> fileList) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.createFeedback(courseId, feedbackRequest, fileList)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<Page<FeedBackResponse>>> getByCourse(
            @PathVariable UUID courseId, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getFeedbakcCourse(courseId, page, size)));
    }

    @GetMapping("/can-feedback/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> canFeedback(@PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.canFeedback(courseId)));
    }

    @GetMapping("/can-edit/{feedbackId}")
    public ResponseEntity<ApiResponse<Boolean>> canEdit(@PathVariable UUID feedbackId) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.canEditAndDelete(feedbackId)));
    }

    @PutMapping(value = "/{feedbackId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FeedBackResponse>> update(
            @PathVariable UUID feedbackId,
            @RequestPart UpdateFeedbackRequest request,
            @RequestPart(required = false) List<MultipartFile> fileList) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.updateFeedback(request, feedbackId, fileList)));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.ok(ApiResponse.success("Delete feedback successfully"));
    }
}