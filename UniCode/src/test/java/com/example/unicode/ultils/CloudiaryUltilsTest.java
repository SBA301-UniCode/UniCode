package com.example.unicode.ultils;

import com.example.unicode.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudiaryUltilsTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CloudiaryUltils ultils;

    @Test
    void getUrlCloudiaryShouldUseUploadDocumentForImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinaryService.uploadDocument(file)).thenReturn(Map.of("public_id", "pid", "secure_url", "url"));

        var result = ultils.getUrlCloudiary(file, "image");

        assertEquals("pid", result.get(0));
        assertEquals("url", result.get(1));
        verify(cloudinaryService).uploadDocument(file);
        verify(cloudinaryService, never()).uploadVideo(any());
    }

    @Test
    void getUrlCloudiaryShouldUseUploadVideoForVideo() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinaryService.uploadVideo(file)).thenReturn(Map.of("public_id", "vpid", "secure_url", "vurl"));

        var result = ultils.getUrlCloudiary(file, "video");

        assertEquals("vpid", result.get(0));
        assertEquals("vurl", result.get(1));
        verify(cloudinaryService).uploadVideo(file);
        verify(cloudinaryService, never()).uploadDocument(any());
    }

    @Test
    void getUrlCloudiaryShouldTreatVideoTypeCaseInsensitively() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinaryService.uploadVideo(file)).thenReturn(Map.of("public_id", "vpid", "secure_url", "vurl"));

        var result = ultils.getUrlCloudiary(file, "VIDEO");

        assertEquals("vpid", result.get(0));
        assertEquals("vurl", result.get(1));
    }

    @Test
    void getUrlCloudiaryShouldDefaultToEmptyWhenFieldsMissing() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(cloudinaryService.uploadDocument(file)).thenReturn(Map.of("ignored", "value"));

        var result = ultils.getUrlCloudiary(file, "document");

        assertEquals("", result.get(0));
        assertEquals("", result.get(1));
    }
}
