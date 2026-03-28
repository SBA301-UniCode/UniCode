package com.example.unicode.controller;

import com.example.unicode.service.CloudinaryService;
import com.example.unicode.service.impl.VideoServiceImpl;
import com.example.unicode.ultils.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoServiceImpl videoService;
    @Mock
    private S3Service s3Service;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private VideoController controller;

    @Test
    void getVideoShouldDelegate() {
        UUID videoId = UUID.randomUUID();
        when(videoService.getUrlToShow(videoId)).thenReturn(null);

        var response = controller.getVideo(videoId);

        assertEquals(200, response.getStatusCode().value());
        verify(videoService).getUrlToShow(videoId);
    }
}

