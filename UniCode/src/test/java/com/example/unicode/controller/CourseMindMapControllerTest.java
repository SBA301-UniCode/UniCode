package com.example.unicode.controller;

import com.example.unicode.service.impl.CourseMindMapServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseMindMapControllerTest {

    @Mock
    private CourseMindMapServiceImpl mindMapService;

    @InjectMocks
    private CourseMindMapController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTreeShouldReturnOkWhenServiceSucceeds() {
        UUID courseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("u@test.com", "pwd"));
        when(mindMapService.getUserIdByEmail("u@test.com")).thenReturn(userId);
        when(mindMapService.getUserTree(userId, courseId)).thenReturn("{}");

        var response = controller.getTree(courseId);

        assertEquals(200, response.getStatusCode().value());
        verify(mindMapService).getUserTree(userId, courseId);
    }
}

