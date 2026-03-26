package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.response.WatermarkDownloadResult;
import com.example.unicode.dto.response.WatermarkVerifyResponse;
import com.example.unicode.service.WatermarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watermark")
@RequiredArgsConstructor
@Tag(name = "Watermark", description = "Document watermark & verification APIs")
public class WatermarkController {

    private final WatermarkService watermarkService;

    /**
     * Download a document with invisible watermark embedded (requires auth).
     * The watermark contains the current user's ID and email for leak tracing.
     */
    @GetMapping("/download/{documentId}")
    @Operation(summary = "Download document with embedded watermark")
    public ResponseEntity<byte[]> downloadWithWatermark(@PathVariable UUID documentId) {
        WatermarkDownloadResult result = watermarkService.downloadWithWatermark(documentId);

        // RFC 5987: encode filename for non-ASCII characters
        String rawName = result.getFileName();
        String asciiName = rawName.replaceAll("[^\\x20-\\x7E]", "_"); // fallback for old browsers
        String encodedName;
        try {
            encodedName = java.net.URLEncoder.encode(rawName, "UTF-8").replace("+", "%20");
        } catch (java.io.UnsupportedEncodingException e) {
            encodedName = asciiName;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + asciiName + "\"; filename*=UTF-8''" + encodedName)
                .body(result.getFileBytes());
    }

    /**
     * Upload an image/PDF to verify if it contains a watermark or matches
     * a previously stored fingerprint. (Admin/Instructor only)
     */
    @PostMapping("/verify")
    @Operation(summary = "Verify uploaded file for watermark or fingerprint match")
    public ResponseEntity<ApiResponse<WatermarkVerifyResponse>> verify(
            @RequestParam("file") MultipartFile file) {
        WatermarkVerifyResponse result = watermarkService.verify(file);
        return ResponseEntity.ok(ApiResponse.success("Verification completed", result));
    }
}

