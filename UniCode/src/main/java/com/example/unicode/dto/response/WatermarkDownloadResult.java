package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wraps the watermarked file bytes together with metadata so the
 * controller can set the correct Content-Type and filename headers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkDownloadResult {
    private byte[] fileBytes;
    private String fileName;
    private String contentType;
}
