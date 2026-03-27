package com.example.unicode.controller;

import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.SylabusResponse;
import com.example.unicode.service.SylabusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SylabusControllerTest {

    @Mock
    private SylabusService sylabusService;

    @InjectMocks
    private SylabusController controller;

    @Test
    void createShouldReturnCreated() {
        when(sylabusService.create(null)).thenReturn(new SylabusResponse());

        var response = controller.create(null);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Syllabus created successfully", response.getBody().getMessage());
        verify(sylabusService).create(null);
    }

    @Test
    void getByIdShouldDelegate() {
        when(sylabusService.getById("S1")).thenReturn(new SylabusResponse());

        var response = controller.getById("S1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Syllabus retrieved successfully", response.getBody().getMessage());
        verify(sylabusService).getById("S1");
    }

    @Test
    void getAllShouldDelegate() {
        PageResponse<SylabusResponse> page = PageResponse.<SylabusResponse>builder()
                .content(List.of(new SylabusResponse()))
                .build();
        when(sylabusService.getAll(0, 10)).thenReturn(page);

        var response = controller.getAll(0, 10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().getContent().size());
        verify(sylabusService).getAll(0, 10);
    }

    @Test
    void updateShouldDelegate() {
        when(sylabusService.update("S1", null)).thenReturn(new SylabusResponse());

        var response = controller.update("S1", null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Syllabus updated successfully", response.getBody().getMessage());
        verify(sylabusService).update("S1", null);
    }

    @Test
    void deleteShouldDelegate() {
        var response = controller.delete("S1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Syllabus deleted successfully", response.getBody().getMessage());
        verify(sylabusService).delete("S1");
    }
}
