package com.example.unicode.controller;

import com.example.unicode.dto.request.AICodeAssistantRequest;
import com.example.unicode.dto.request.CourseChatRequest;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIControllerTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AIController controller;

    @Test
    void codeAssistantShouldReturnOkWhenServiceSucceeds() {
        AICodeAssistantRequest request = new AICodeAssistantRequest();
        request.setCode("print('hello')");
        when(geminiService.askCodeAssistant(request)).thenReturn("ai-ok");

        var response = controller.codeAssistant(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("AI response generated", response.getBody().getMessage());
        assertEquals("ai-ok", response.getBody().getData());
    }

    @Test
    void codeAssistantShouldReturn500WhenServiceThrows() {
        AICodeAssistantRequest request = new AICodeAssistantRequest();
        when(geminiService.askCodeAssistant(request)).thenThrow(new RuntimeException("gemini down"));

        var response = controller.codeAssistant(request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals(500, response.getBody().getCode());
        assertEquals("gemini down", response.getBody().getMessage());
    }

    @Test
    void courseChatShouldReturnBadRequestWhenMessageNull() {
        var response = controller.courseChat(new CourseChatRequest(null));

        assertEquals(400, response.getStatusCode().value());
        assertEquals(400, response.getBody().getCode());
        assertEquals("Vui lòng nhập câu hỏi.", response.getBody().getMessage());
    }

    @Test
    void courseChatShouldReturnBadRequestWhenMessageBlank() {
        var response = controller.courseChat(new CourseChatRequest("   "));

        assertEquals(400, response.getStatusCode().value());
        assertEquals(400, response.getBody().getCode());
        assertEquals("Vui lòng nhập câu hỏi.", response.getBody().getMessage());
    }

    @Test
    void courseChatShouldBuildCatalogAndReturnAiResponse() {
        Course c1 = new Course();
        c1.setCourseId(UUID.randomUUID());
        c1.setTitle("Java");
        c1.setPrice(100000d);
        c1.setDescription("A".repeat(120));
        Users instructor = new Users();
        instructor.setName("Teacher A");
        c1.setInstructors(instructor);

        Course c2 = new Course();
        c2.setCourseId(UUID.randomUUID());
        c2.setTitle("Python");
        c2.setPrice(0d);
        c2.setDescription(null);
        c2.setInstructors(null);

        Course c3 = new Course();
        c3.setCourseId(UUID.randomUUID());
        c3.setTitle("Go");
        c3.setPrice(null);
        c3.setDescription("short");
        c3.setInstructors(null);

        when(courseRepository.findAll()).thenReturn(List.of(c1, c2, c3));
        when(geminiService.askCourseRecommendation(eq("goi y khoa hoc"), any())).thenReturn("goi y");

        var response = controller.courseChat(new CourseChatRequest("goi y khoa hoc"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("goi y", response.getBody().getData());

        ArgumentCaptor<String> catalogCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiService).askCourseRecommendation(eq("goi y khoa hoc"), catalogCaptor.capture());
        String catalog = catalogCaptor.getValue();
        assertTrue(catalog.contains("Title: Java"));
        assertTrue(catalog.contains("100000 VND"));
        assertTrue(catalog.contains("Instructor: Teacher A"));
        assertTrue(catalog.contains("Title: Python"));
        assertTrue(catalog.contains("Miễn phí"));
        assertTrue(catalog.contains("Instructor: N/A"));
        assertTrue(catalog.contains("Title: Go"));
        assertTrue(catalog.contains("Miễn phí"));
        assertTrue(catalog.contains("Instructor: N/A"));
    }

    @Test
    void courseChatShouldReturn500WhenGeminiThrows() {
        when(courseRepository.findAll()).thenReturn(List.of());
        when(geminiService.askCourseRecommendation(eq("hoi"), any())).thenThrow(new RuntimeException("ai fail"));

        var response = controller.courseChat(new CourseChatRequest("hoi"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals(500, response.getBody().getCode());
        assertEquals("ai fail", response.getBody().getMessage());
    }
}
