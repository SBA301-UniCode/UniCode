package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.AICodeAssistantRequest;
import com.example.unicode.dto.request.CourseChatRequest;
import com.example.unicode.entity.Course;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "Gemini AI code assistance APIs")
public class AIController {

    private final GeminiService geminiService;
    private final CourseRepository courseRepository;

    @PostMapping("/code-assistant")
    @Operation(summary = "Get AI code assistance for practice exercises")
    public ResponseEntity<ApiResponse<String>> codeAssistant(@RequestBody AICodeAssistantRequest request) {
        try {
            String aiResponse = geminiService.askCodeAssistant(request);
            return ResponseEntity.ok(ApiResponse.success("AI response generated", aiResponse));
        } catch (RuntimeException e) {
            log.error("AI code assistant error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    /**
     * Public course recommendation chatbot — no auth required.
     * Fetches all courses from DB, sends to Gemini with user's question.
     */
    @PostMapping("/course-chat")
    @Operation(summary = "AI chatbot for course recommendations")
    public ResponseEntity<ApiResponse<String>> courseChat(@RequestBody CourseChatRequest request) {
        try {
            if (request.getMessage() == null || request.getMessage().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Vui lòng nhập câu hỏi."));
            }

            // Fetch all courses and build compact catalog
            List<Course> courses = courseRepository.findAll();
            StringBuilder catalog = new StringBuilder();
            for (Course c : courses) {
                String instructor = c.getInstructors() != null ? c.getInstructors().getName() : "N/A";
                String price = c.getPrice() != null && c.getPrice() > 0
                        ? String.format("%.0f VND", c.getPrice())
                        : "Miễn phí";
                String desc = c.getDescription() != null
                        ? c.getDescription().substring(0, Math.min(c.getDescription().length(), 100))
                        : "";
                catalog.append(String.format("[COURSE_ID: %s | Title: %s | Price: %s | Instructor: %s | Desc: %s]\n",
                        c.getCourseId(), c.getTitle(), price, instructor, desc));
            }

            String aiResponse = geminiService.askCourseRecommendation(
                    request.getMessage(), catalog.toString());
            return ResponseEntity.ok(ApiResponse.success("AI response generated", aiResponse));

        } catch (RuntimeException e) {
            log.error("Course chat error: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
}
