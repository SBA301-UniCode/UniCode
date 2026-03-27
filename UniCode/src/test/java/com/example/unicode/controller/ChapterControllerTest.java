package com.example.unicode.controller;

import com.example.unicode.dto.response.ChapterResponse;
import com.example.unicode.service.ChapterService;
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
class ChapterControllerTest {

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterController controller;

    @Test
    void getByIdShouldReturnOk() {
        UUID id = UUID.randomUUID();
        when(chapterService.getById(id)).thenReturn(new ChapterResponse());

        var response = controller.getById(id);

        assertEquals(200, response.getStatusCode().value());
        verify(chapterService).getById(id);
    }
}

