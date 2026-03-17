package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkVerifyResponse {
    private boolean found;
    private String method;
    private double confidence;

    // Từ watermark trực tiếp
    private String userId;
    private String email;
    private String timestamp;

    // Từ fingerprint matching
    private UUID matchedDocumentId;
    private String matchedDocumentTitle;
    private String matchedUserEmail;
    private Instant matchedDownloadedAt;
}
