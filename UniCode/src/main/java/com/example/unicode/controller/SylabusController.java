package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.SylabusCreateRequest;
import com.example.unicode.dto.request.SylabusUpdateRequest;
import com.example.unicode.dto.response.SylabusResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.service.SylabusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Syllabus management.
 * Follows RESTful conventions as per PROJECT_SOP:
 * - Uses plural nouns for resources
 * - Uses HTTP methods correctly (GET, POST, PUT, DELETE)
 * - Returns unified ApiResponse wrapper
 */
@RestController
@RequestMapping("/api/v1/syllabuses")
@RequiredArgsConstructor
@Tag(name = "Syllabus", description = "Syllabus management APIs")
public class SylabusController {

    private final SylabusService sylabusService;

    @PostMapping
    @Operation(summary = "Create a new syllabus")
    public ResponseEntity<ApiResponse<SylabusResponse>> create(
            @Valid @RequestBody SylabusCreateRequest request) {
        SylabusResponse response = sylabusService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Syllabus created successfully", response));
    }

    @GetMapping("/{sylabusId}")
    @Operation(summary = "Get syllabus by ID")
    public ResponseEntity<ApiResponse<SylabusResponse>> getById(
            @PathVariable String sylabusId) {
        SylabusResponse response = sylabusService.getById(sylabusId);
        return ResponseEntity.ok(ApiResponse.success("Syllabus retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all syllabuses with pagination")
    public ResponseEntity<ApiResponse<PageResponse<SylabusResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SylabusResponse> response = sylabusService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Syllabuses retrieved successfully", response));
    }

    @PutMapping("/{sylabusId}")
    @Operation(summary = "Update syllabus by ID")
    public ResponseEntity<ApiResponse<SylabusResponse>> update(
            @PathVariable String sylabusId,
            @Valid @RequestBody SylabusUpdateRequest request) {
        SylabusResponse response = sylabusService.update(sylabusId, request);
        return ResponseEntity.ok(ApiResponse.success("Syllabus updated successfully", response));
    }

    @DeleteMapping("/{sylabusId}")
    @Operation(summary = "Delete syllabus by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String sylabusId) {
        sylabusService.delete(sylabusId);
        return ResponseEntity.ok(ApiResponse.success("Syllabus deleted successfully"));
    }
}
