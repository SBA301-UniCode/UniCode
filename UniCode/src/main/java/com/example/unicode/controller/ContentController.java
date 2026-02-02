package com.example.unicode.controller;


import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ContentCreateRequest;
import com.example.unicode.dto.request.ContentUpdateRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.dto.response.ContentResponse;
import com.example.unicode.entity.Lesson;
import com.example.unicode.service.impl.ContentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
@Tag(name = "Content", description = "Content management APIs")
public class ContentController {
    private final ContentServiceImpl contentService;

    @PostMapping
    @Operation(summary = "Create a new content")
    public ResponseEntity<ApiResponse<ContentResponse>> create(
            @Valid @RequestBody ContentCreateRequest request
            ){
        ContentResponse response = contentService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED).
                body(ApiResponse.success("Content created successfully", response));

    }
    @GetMapping("/{lesonId}")
    @Operation(summary = "Get all content")
    public ResponseEntity<ApiResponse<List<ContentResponse>>> getAll(@PathVariable UUID lesonId) {
        List<ContentResponse> response = contentService.getAllContentAndLesson(lesonId);
        return ResponseEntity.ok(ApiResponse.success("Get list Content successfully", response));
    }

    @PutMapping("/{contentId}")
    @Operation(summary = "Update content by ID")
    public ResponseEntity<ApiResponse<ContentResponse>> update(
            @PathVariable UUID contentId,
            @Valid @RequestBody ContentUpdateRequest request
    ) {
        ContentResponse response = contentService.update(contentId, request);
        return ResponseEntity.ok(ApiResponse.success("Content updated successfully", response));
    }


    @DeleteMapping("/{contentId}")
    @Operation(summary = "Delete leson by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID contentId) {
        contentService.delete(contentId);
        return ResponseEntity.ok(ApiResponse.success("Content deleted successfully"));
    }






}
