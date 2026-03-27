package com.example.unicode.controller;

import com.example.unicode.service.DocumentService;
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
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController controller;

    @Test
    void getAllByLessonIdShouldDelegate() {
        UUID lessonId = UUID.randomUUID();
        when(documentService.getAllDocumentByLessonId(lessonId)).thenReturn(List.of());

        var response = controller.getAllByLessonId(lessonId);

        assertEquals(200, response.getStatusCode().value());
        verify(documentService).getAllDocumentByLessonId(lessonId);
    }
}

