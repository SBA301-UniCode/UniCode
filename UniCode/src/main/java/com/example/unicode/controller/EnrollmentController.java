package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.EnrollementBannerRequest;
import com.example.unicode.dto.request.SearchEnrollRequest;
import com.example.unicode.dto.request.UpdateEnrollmentRequest;
import com.example.unicode.dto.response.EnrollmentReportResponse;
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
    @GetMapping("/courses/{courseId}")
    @Operation(summary = "Get all enrollments by ID")
    public ResponseEntity<ApiResponse<Page<EnrolmentResponse>>> getById(
            @PathVariable UUID courseId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0",required = false) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10",required = false) int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.getAllByCourse(courseId,page,size)));
    }
    @PostMapping("/courses/{courseId}")
    @Operation(summary = "Enroll couser by Id")
    public ResponseEntity<ApiResponse<EnrolmentResponse>> join(
            @PathVariable UUID courseId
    ){
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.joinCousera(courseId)));
    }
    @GetMapping("/courses/{courseId}/me")
    @Operation(summary = "Check this user enrolled or not")
    public ResponseEntity<ApiResponse<Boolean>> isEnrolled(
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(enrollmentService.isEnrolled(courseId)));
    }
    @PostMapping("/is-banned")
    public ResponseEntity<ApiResponse<Boolean>> isBanned(
            @RequestBody EnrollementBannerRequest request
            ){
        return  ResponseEntity.ok(ApiResponse.success(enrollmentService.isbanned(request)));
    }
    @PostMapping("/ban-learner")
    public ResponseEntity<ApiResponse<Void>> banUser(
            @RequestBody EnrollementBannerRequest request
    ) {
        enrollmentService.banLearner(request,true);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @PostMapping("/open-ban-learner")
    public ResponseEntity<ApiResponse<Void>> openBand(
            @RequestBody EnrollementBannerRequest request
    ) {
        enrollmentService.banLearner(request,false);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @GetMapping("/learners/{coureId}")
    public ResponseEntity<ApiResponse<Page<EnrollmentReportResponse>>> reportEnrollmentS(
            @PathVariable UUID coureId,
            @RequestParam(required = false) String keysearch,
            @RequestParam(defaultValue = "false",required = false) boolean banned,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0",required = false) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10",required = false) int size
    ){
        return  ResponseEntity.ok(ApiResponse.success(enrollmentService.getReportEnrollmentFollowByCourse(coureId,keysearch,banned,page,size)));
    }
}
