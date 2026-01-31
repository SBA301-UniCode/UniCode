package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.dto.request.ChapterUpdateRequest;
import com.example.unicode.dto.response.ChapterResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.service.ChapterService;
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
 * REST Controller for Chapter management.
 * Follows RESTful conventions as per PROJECT_SOP:
 * - Uses plural nouns for resources
 * - Uses HTTP methods correctly (GET, POST, PUT, DELETE)
 * - Returns unified ApiResponse wrapper
 */
@RestController
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter", description = "Chapter management APIs")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @Operation(summary = "Create a new chapter")
    public ResponseEntity<ApiResponse<ChapterResponse>> create(
            @Valid @RequestBody ChapterCreateRequest request) {
        ChapterResponse response = chapterService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chapter created successfully", response));
    }

    @GetMapping("/{chapterId}")
    @Operation(summary = "Get chapter by ID")
    public ResponseEntity<ApiResponse<ChapterResponse>> getById(
            @PathVariable UUID chapterId) {
        ChapterResponse response = chapterService.getById(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Chapter retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all chapters with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ChapterResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ChapterResponse> response = chapterService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Chapters retrieved successfully", response));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all chapters by course ID (ordered by orderIndex)")
    public ResponseEntity<ApiResponse<List<ChapterResponse>>> getByCourseId(
            @PathVariable UUID courseId) {
        List<ChapterResponse> response = chapterService.getByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success("Chapters retrieved successfully", response));
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "Update chapter by ID")
    public ResponseEntity<ApiResponse<ChapterResponse>> update(
            @PathVariable UUID chapterId,
            @Valid @RequestBody ChapterUpdateRequest request) {
        ChapterResponse response = chapterService.update(chapterId, request);
        return ResponseEntity.ok(ApiResponse.success("Chapter updated successfully", response));
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "Delete chapter by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID chapterId) {
        chapterService.delete(chapterId);
        return ResponseEntity.ok(ApiResponse.success("Chapter deleted successfully"));
    }
}
