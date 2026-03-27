package com.example.unicode.controller;

import com.example.unicode.dto.response.WatermarkDownloadResult;
import com.example.unicode.dto.response.WatermarkVerifyResponse;
import com.example.unicode.service.WatermarkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatermarkControllerTest {

    @Mock
    private WatermarkService watermarkService;

    @InjectMocks
    private WatermarkController controller;

    @Test
    void verifyShouldDelegate() {
        MultipartFile file = mock(MultipartFile.class);
        when(watermarkService.verify(file)).thenReturn(WatermarkVerifyResponse.builder().found(false).build());

        var response = controller.verify(file);

        assertEquals(200, response.getStatusCode().value());
        verify(watermarkService).verify(file);
    }

    @Test
    void downloadWithWatermarkShouldSetHeadersAndBody() {
        UUID documentId = UUID.randomUUID();
        byte[] fileBytes = "content".getBytes();
        WatermarkDownloadResult result = WatermarkDownloadResult.builder()
                .fileBytes(fileBytes)
                .fileName("abc.pdf")
                .contentType("application/pdf")
                .build();
        when(watermarkService.downloadWithWatermark(documentId)).thenReturn(result);

        var response = controller.downloadWithWatermark(documentId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(contentDisposition.contains("attachment; filename=\"abc.pdf\""));
        assertTrue(contentDisposition.contains("filename*=UTF-8''abc.pdf"));
        assertArrayEquals(fileBytes, response.getBody());
        verify(watermarkService).downloadWithWatermark(documentId);
    }
}
