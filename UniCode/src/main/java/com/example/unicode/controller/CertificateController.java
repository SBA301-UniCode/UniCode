package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.CertificateCreateRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.service.CertificateService;
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
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificate", description = "Certificate management APIs")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    @Operation(summary = "Create a new certificate")
    public ResponseEntity<ApiResponse<CertificateResponse>> create(
            @Valid @RequestBody CertificateCreateRequest request) {
        CertificateResponse response = certificateService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certificate created successfully", response));
    }

    @GetMapping("/{certificateId}")
    @Operation(summary = "Get certificate by ID")
    public ResponseEntity<ApiResponse<CertificateResponse>> getById(
            @PathVariable UUID certificateId) {
        CertificateResponse response = certificateService.getById(certificateId);
        return ResponseEntity.ok(ApiResponse.success("Certificate retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all certificates")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getAll() {
        List<CertificateResponse> response = certificateService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Certificates retrieved successfully", response));
    }

    @GetMapping("/user/{learnerId}")
    @Operation(summary = "Get certificates by learner ID")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getByLearnerId(
            @PathVariable UUID learnerId) {
        List<CertificateResponse> response = certificateService.getByLearnerId(learnerId);
        return ResponseEntity.ok(ApiResponse.success("Certificates retrieved successfully", response));
    }

    @DeleteMapping("/{certificateId}")
    @Operation(summary = "Delete certificate by ID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID certificateId) {
        certificateService.delete(certificateId);
        return ResponseEntity.ok(ApiResponse.success("Certificate deleted successfully"));
    }
}

