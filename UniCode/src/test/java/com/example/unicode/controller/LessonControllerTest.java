package com.example.unicode.controller;

import com.example.unicode.dto.response.LessonResponse;
import com.example.unicode.service.LessonService;
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
class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController controller;

    @Test
    void getByIdShouldDelegate() {
        UUID lessonId = UUID.randomUUID();
        when(lessonService.getById(lessonId)).thenReturn(new LessonResponse());

        var response = controller.getById(lessonId);

        assertEquals(200, response.getStatusCode().value());
        verify(lessonService).getById(lessonId);
    }
}

