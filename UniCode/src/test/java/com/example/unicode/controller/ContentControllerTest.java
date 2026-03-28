package com.example.unicode.controller;

import com.example.unicode.service.impl.ContentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    @Mock
    private ContentServiceImpl contentService;

    @InjectMocks
    private ContentController controller;

    @Test
    void getAllShouldDelegate() {
        UUID lessonId = UUID.randomUUID();
        when(contentService.getAllContentAndLesson(lessonId)).thenReturn(List.of());

        var response = controller.getAll(lessonId);

        assertEquals(200, response.getStatusCode().value());
        verify(contentService).getAllContentAndLesson(lessonId);
    }
}

