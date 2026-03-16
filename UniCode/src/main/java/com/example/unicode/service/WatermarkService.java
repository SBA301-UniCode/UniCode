package com.example.unicode.service;

import com.example.unicode.dto.response.WatermarkDownloadResult;
import com.example.unicode.dto.response.WatermarkVerifyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface WatermarkService {
    /**
     * Download document with embedded invisible watermark + store fingerprints
     */
    WatermarkDownloadResult downloadWithWatermark(UUID documentId);

    /**
     * Verify uploaded file – extract watermark or match fingerprint
     */
    WatermarkVerifyResponse verify(MultipartFile file);
}
