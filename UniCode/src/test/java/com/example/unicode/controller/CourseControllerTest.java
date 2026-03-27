package com.example.unicode.controller;

import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.service.CourseService;
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
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController controller;

    @Test
    void getByIdShouldReturnOk() {
        UUID id = UUID.randomUUID();
        when(courseService.getById(id)).thenReturn(new CourseResponse());

        var response = controller.getById(id);

        assertEquals(200, response.getStatusCode().value());
        verify(courseService).getById(id);
    }
}

