package com.example.unicode.service;

import com.example.unicode.dto.request.AICodeAssistantRequest;

public interface GeminiService {

    /**
     * Send a code assistance request to Gemini AI and return the AI's text response.
     *
     * @param request the code, language, mode, and optionally test results
     * @return AI-generated text response in Vietnamese
     */
    String askCodeAssistant(AICodeAssistantRequest request);

    /**
     * Send a course recommendation chat request to Gemini AI.
     *
     * @param userMessage   the user's question
     * @param coursesCatalog formatted string of all available courses
     * @return AI-generated course recommendation in Vietnamese
     */
    String askCourseRecommendation(String userMessage, String coursesCatalog);
}
