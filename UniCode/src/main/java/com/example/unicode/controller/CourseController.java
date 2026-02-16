package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.service.CourseService;
import com.example.unicode.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Course management.
 * Follows RESTful conventions as per PROJECT_SOP:
 * - Uses plural nouns for resources
 * - Uses HTTP methods correctly (GET, POST, PUT, DELETE)
 * - Returns unified ApiResponse wrapper
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "Course management APIs")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @Valid @RequestBody CourseCreateRequest request) {
        CourseResponse response = courseService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", response));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<ApiResponse<CourseResponse>> getById(
            @PathVariable UUID courseId) {
        CourseResponse response = courseService.getById(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all courses with pagination")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseResponse> response = courseService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", response));
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Update course by ID")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        CourseResponse response = courseService.update(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", response));
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete course by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID courseId) {
        courseService.delete(courseId);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully"));
    }

}
