package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.dto.request.LessonUpdateRequest;
import com.example.unicode.dto.response.LessonResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Lesson management.
 * Follows RESTful conventions as per PROJECT_SOP:
 * - Uses plural nouns for resources
 * - Uses HTTP methods correctly (GET, POST, PUT, DELETE)
 * - Returns unified ApiResponse wrapper
 */
@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson", description = "Lesson management APIs")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create a new lesson")
    public ResponseEntity<ApiResponse<LessonResponse>> create(
            @Valid @RequestBody LessonCreateRequest request) {
        LessonResponse response = lessonService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson created successfully", response));
    }

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson by ID")
    public ResponseEntity<ApiResponse<LessonResponse>> getById(
            @PathVariable UUID lessonId) {
        LessonResponse response = lessonService.getById(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Lesson retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all lessons with pagination")
    public ResponseEntity<ApiResponse<PageResponse<LessonResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<LessonResponse> response = lessonService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lessons retrieved successfully", response));
    }

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "Get all lessons by chapter ID (ordered by orderIndex)")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getByChapterId(
            @PathVariable UUID chapterId) {
        List<LessonResponse> response = lessonService.getByChapterId(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Lessons retrieved successfully", response));
    }

    @PutMapping("/{lessonId}")
    @Operation(summary = "Update lesson by ID")
    public ResponseEntity<ApiResponse<LessonResponse>> update(
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonUpdateRequest request) {
        LessonResponse response = lessonService.update(lessonId, request);
        return ResponseEntity.ok(ApiResponse.success("Lesson updated successfully", response));
    }

    @DeleteMapping("/{lessonId}")
    @Operation(summary = "Delete lesson by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID lessonId) {
        lessonService.delete(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted successfully"));
    }
}
