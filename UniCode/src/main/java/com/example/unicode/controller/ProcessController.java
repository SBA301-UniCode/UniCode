package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ProcessRequest;
import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.entity.Lesson;
import com.example.unicode.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/process")
@RequiredArgsConstructor
@Tag(name = "Process", description = "Process management APIs")
public class ProcessController {
    private final ProcessService processService;
    @PostMapping("/tracking")
    public ResponseEntity<ApiResponse<TrackingResponse.ProcessResponse>> tracking(@RequestBody ProcessRequest request) {
        return ResponseEntity.ok(ApiResponse.success(processService.trackProcessContent(request)));
    }

    @PostMapping("/lessons")
    @Operation(summary = "Get all content process of  Lesson")
    public ResponseEntity<ApiResponse<TrackingResponse>> processLesson(@RequestBody TrackingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(processService.getProcessOfLesson(request)));
    }
    @PostMapping("/chapters")
    @Operation(summary = "Get all Lesson process of Chapter")
    public ResponseEntity<ApiResponse<TrackingResponse>> processChapter(@RequestBody TrackingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(processService.getProcessOfChapter(request)));
    }
    @PostMapping("/courses")
    @Operation(summary = "Get all Chapter process of Course")
    public ResponseEntity<ApiResponse<TrackingResponse>> processCourse(@RequestBody TrackingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(processService.getProcessOfCourses(request)));
    }
}
