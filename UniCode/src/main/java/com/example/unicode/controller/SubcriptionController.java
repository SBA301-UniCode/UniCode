package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.SearchSubcriptionRequest;
import com.example.unicode.dto.request.SubcriptionReportRequest;
import com.example.unicode.dto.response.SubcriptionReportResponse;
import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.entity.Subcription;
import com.example.unicode.service.SubcriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscriptions management APIs")
public class SubcriptionController {
    private final SubcriptionService subcriptionService;

    @PostMapping("/buy/{courserId}")
    @Operation(summary = "Buy course subscription")
    public ResponseEntity<ApiResponse<String>> buy(@PathVariable("courserId") UUID courserId) {
        return ResponseEntity.ok(ApiResponse.success(subcriptionService.buyCourses(courserId)));
    }
    // redirect FE
    @GetMapping("/momo/call-back")
    @Operation(summary = "Handle Momo payment callback")
    public ResponseEntity<ApiResponse<SubcriptionResponse>> callback(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(subcriptionService.updateStatus(request)));
    }
    @PostMapping("/search")
    @Operation(    summary = "Get subscriptions with optional filters and pagination")
    public ResponseEntity<ApiResponse<Page<SubcriptionResponse>>> getAll(
            @RequestBody(required = false) SearchSubcriptionRequest request,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0",required = false) int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10",required = false) int size
    ) {
    return ResponseEntity.ok(ApiResponse.success(subcriptionService.getAll(request,page,size)));
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/report")
    @Operation(summary = "Subscription report")
    public ResponseEntity<ApiResponse<SubcriptionReportResponse>> report(@RequestBody  SubcriptionReportRequest request)
    {
        return ResponseEntity.ok(ApiResponse.success(subcriptionService.report(request)));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<ApiResponse<SubcriptionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(subcriptionService.getById(id)));
    }

}
