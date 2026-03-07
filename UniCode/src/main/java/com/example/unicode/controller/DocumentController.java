package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.dto.response.DocumentResponse;
import com.example.unicode.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document", description = "Document management APIs")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/create")
    @Operation(summary = "Create a new document")
    public ResponseEntity<ApiResponse<DocumentResponse>> create(
            @RequestBody @Valid DocumentCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Document created successfully",
                documentService.create(request)
        ));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all documents by Lesson ID")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllByLessonId(
            @PathVariable UUID lessonId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Get list documents successfully",
                documentService.getAllDocumentByLessonId(lessonId)
        ));
    }

    @PutMapping("/{documentId}")
    @Operation(summary = "Update document")
    public ResponseEntity<ApiResponse<DocumentResponse>> update(
            @PathVariable UUID documentId,
            @RequestBody @Valid DocumentUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Document updated successfully",
                documentService.update(documentId, request)
        ));
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete document")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID documentId) {
        documentService.delete(documentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Document deleted successfully")
                .build());
    }
}