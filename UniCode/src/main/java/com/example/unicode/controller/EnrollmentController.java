package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.SearchEnrollRequest;
import com.example.unicode.dto.request.UpdateEnrollmentRequest;
import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "Enrollment management APIs")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/search")
    @Operation(summary = "Search enrollments by course title, descriptions, status enrollments, learner id, course id")
    public ResponseEntity<ApiResponse<Page<EnrolmentResponse>>> search(
            @RequestBody SearchEnrollRequest request,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0",required = false) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10",required = false) int size
            ){
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.search(request,page,size)));
    }
    @GetMapping("/me")
    @Operation(summary = "Get all course joined and filter by status")
    public ResponseEntity<ApiResponse<Page<EnrolmentResponse>>> getMyLearning(
            @RequestParam StatusCourse statusCourse,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0",required = false) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10",required = false) int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.myLearn(statusCourse,page,size)));
    }
    @PatchMapping("/update")
    @Operation(summary = "Update status process learning by ID")
    public ResponseEntity<ApiResponse<EnrolmentResponse>> update(
            @RequestBody UpdateEnrollmentRequest request
    ){
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.update(request)));
    }

}
